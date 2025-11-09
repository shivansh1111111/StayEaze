package com.business.booking.controllers;

import com.business.booking.dto.BookingDto;
import com.business.booking.dto.Response;
import com.business.booking.service.BookingService;
import com.business.booking.util.dataclasses.Responses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/businessCompany")
@Slf4j
public class BookingController {

    @Autowired
    BookingService bookingService;
    @GetMapping(path = "/ping")
    public ResponseEntity<Response> ping(){
        log.info("Testing logs using slf4j");
        Response response = new Response();
        response.setResponse(Responses.SUCCESS);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping(path = "/bookings")
    public ResponseEntity<Response> bookings(@RequestParam String userId){
        return new ResponseEntity<>(bookingService.getUserBookings(userId), HttpStatus.OK);
    }
    @GetMapping(path = "/bookings/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable String bookingId){
        return new ResponseEntity<>(bookingService.getBooking(bookingId), HttpStatus.OK);
    }
    @PostMapping(path = "/bookings")
    public ResponseEntity<BookingDto> createBooking(@RequestBody BookingDto bookingDto){
        log.info("Inside create Booking Controller");
        return new ResponseEntity<>(bookingService.createBooking(bookingDto), HttpStatus.OK);
    }
}
