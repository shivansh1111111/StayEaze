package com.business.booking.database.repositories;

import com.business.booking.database.entities.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlacePerkRepository extends JpaRepository<Place, Integer> {
}
