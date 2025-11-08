package com.business.booking.database.entities;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BOOKINGS")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "BOOKING_ID", updatable = false, nullable = false)
    private String bookingId;

    @Column(name = "USER_ID", nullable = false)
    private String userId;

    @Column(name = "PLACE_ID", nullable = false)
    private String placeId;

    @Column(name = "CHECK_IN", nullable = false)
    private LocalDate checkIn;

    @Column(name = "CHECK_OUT", nullable = false)
    private LocalDate checkOut;

    @Column(name = "PRICE")
    private BigDecimal price;

    // Optionally, you can add relationships to User and Place if you create User entity

    // Getters and Setters
}
