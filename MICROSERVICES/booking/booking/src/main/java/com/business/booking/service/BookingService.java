package com.business.booking.service;

import com.business.booking.dto.BookingDto;
import com.business.booking.dto.BookingListDto;

public interface BookingService {
    public BookingListDto getUserBookings(String usrId);
    public BookingDto getBooking(String bookingId);
    public BookingDto createBooking(BookingDto bookingDto);
}
