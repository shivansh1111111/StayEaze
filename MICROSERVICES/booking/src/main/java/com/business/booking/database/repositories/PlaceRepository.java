package com.business.booking.database.repositories;

import com.business.booking.database.entities.Place;
import com.business.booking.dto.PlaceAutoSearchDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Integer> {
    Optional<Place> findByPlaceId(String placeId);

    List<Place> findAllByOwnerId(String userId);

    @Query(value = "SELECT \n" +
            "    p.place_id,\n" +
            "    p.title,\n" +
            "    pp.url AS first_image_url\n" +
            "FROM\n" +
            "    places p\n" +
            "LEFT JOIN (\n" +
            "    SELECT \n" +
            "        place_id,\n" +
            "        url,\n" +
            "        ROW_NUMBER() OVER (PARTITION BY place_id ORDER BY created_at) AS rn\n" +
            "    FROM place_photos\n" +
            ") pp\n" +
            "    ON p.place_id = pp.place_id AND pp.rn = 1", nativeQuery = true)
    List<Object[]> findAllForAutorsearch();
}
