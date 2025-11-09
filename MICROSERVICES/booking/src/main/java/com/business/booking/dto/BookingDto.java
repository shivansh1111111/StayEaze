package com.business.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data                   // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor       // Generates a no-argument constructor
@AllArgsConstructor      // Generates an all-argument constructor
@Builder                 // Enables builder pattern for object creation
public class BookingDto{
    private String bookingId;
    private String userId;
    private String placeId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String phone;
    private BigDecimal price;
    private PlaceDto place;
}

