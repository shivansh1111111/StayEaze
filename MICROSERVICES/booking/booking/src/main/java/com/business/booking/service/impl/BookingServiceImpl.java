package com.business.booking.service.impl;

import com.business.booking.database.RepositoryFactory;
import com.business.booking.database.entities.Booking;
import com.business.booking.database.entities.User;
import com.business.booking.dto.BookingDto;
import com.business.booking.dto.BookingListDto;
import com.business.booking.dto.PlaceDto;
import com.business.booking.events.EmailNotificationEvent;
import com.business.booking.service.BookingService;
import com.business.booking.service.PlaceService;
import com.business.booking.util.dataclasses.InlineContent;
import com.business.booking.util.dataclasses.NotificationRequest;
import com.business.booking.util.dataclasses.UserContext;
import com.business.booking.util.dataclasses.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    PlaceService placeService;
    @Autowired
    RepositoryFactory repositoryFactory;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Override
    public BookingListDto getUserBookings(String userId) {
        log.info("Fetching bookings for userId: {}", userId);

        List<BookingDto> bookingDtos = repositoryFactory.getBoookingRepository().findAllByUserId(userId)
                .stream()
                .peek(booking -> log.debug("Processing booking: {}", booking.getBookingId()))
                .map(booking -> {
                    PlaceDto place = placeService.getPlace(booking.getPlaceId());
                    return BookingDto.builder()
                            .bookingId(booking.getBookingId())
                            .userId(booking.getUserId())
                            .placeId(booking.getPlaceId())
                            .checkIn(booking.getCheckIn())
                            .checkOut(booking.getCheckOut())
                            .price(booking.getPrice())
                            .place(place)
                            .build();
                })
                .collect(Collectors.toList());

        BookingListDto listDto = new BookingListDto();
        listDto.setBookings(bookingDtos);

        log.info("Total bookings found: {}", bookingDtos.size());
        return listDto;
    }

    @Override
    public BookingDto getBooking(String bookingId) {
        log.info("Fetching booking for bookingId: {}", bookingId);

        Booking booking = repositoryFactory.getBoookingRepository().findByBookingId(bookingId);
        return BookingDto.builder()
                .bookingId(booking.getBookingId())
                .userId(booking.getUserId())
                .placeId(booking.getPlaceId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .price(booking.getPrice())
                .place(placeService.getPlace(booking.getPlaceId()))
                .build();
    }

    @Override
    public BookingDto createBooking(BookingDto bookingDto) {

        Booking booking = getBooking(bookingDto);

        // 2️⃣ Save entity to DB
        booking = repositoryFactory.getBoookingRepository().save(booking);

        // 3️⃣ Optionally fetch place details if needed
        PlaceDto placeDto = placeService.getPlace(booking.getPlaceId());

        User loggedInUser = repositoryFactory.getUserRepository()
                .findById(UserContextHolder.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("Logged-in user not found"));

        // Build NotificationRequest
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setTo(loggedInUser.getEmail());
        notificationRequest.setEmailCode("placeBooked");
        notificationRequest.setInlineContents(List.of(
                new InlineContent("businessUserFirstName", "TEXT", loggedInUser.getFullName()),
                new InlineContent("bookingId", "TEXT", booking.getBookingId()),
                new InlineContent("placeTitle", "TEXT", placeDto.getTitle()),
                new InlineContent("checkInDate", "TEXT", booking.getCheckIn().toString()),
                new InlineContent("checkOutDate", "TEXT", booking.getCheckOut().toString()),
                new InlineContent("price", "TEXT", String.valueOf(booking.getPrice())),
                new InlineContent("maxGuests", "TEXT", String.valueOf(placeDto.getMaxGuests()))
        ));

        // Fire event after commit (new transaction)
        eventPublisher.publishEvent(new EmailNotificationEvent(this, notificationRequest));

        // 4️⃣ Convert saved entity back to DTO
        return BookingDto.builder()
                .bookingId(booking.getBookingId())
                .userId(booking.getUserId())
                .placeId(booking.getPlaceId())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .price(booking.getPrice())
                .place(placeDto) // optional
                .build();
    }

    private static Booking getBooking(BookingDto bookingDto) {
        String userId = UserContextHolder.getCurrentUserId();

        // 1️⃣ Create Booking entity from BookingDto
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setPlaceId(bookingDto.getPlaceId());
        booking.setCheckIn(bookingDto.getCheckIn());
        booking.setCheckOut(bookingDto.getCheckOut());
        booking.setPrice(bookingDto.getPrice());
        return booking;
    }
}
