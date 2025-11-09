package com.business.booking.database.repositories;

import com.business.booking.database.entities.Booking;
import com.business.booking.dto.PlaceDto;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByUserId(String userId);

    Booking findByBookingId(String bookingId);
}
