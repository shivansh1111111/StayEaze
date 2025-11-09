package com.business.booking.database;

import com.business.booking.database.entities.Booking;
import com.business.booking.database.repositories.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RepositoryFactory {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    BoookingRepository boookingRepository;
    @Autowired
    PlaceRepository placeRepository;
    @Autowired
    PlacePhotoRepository placePhotoRepository;
    @Autowired
    PlacePerkRepository placePerkRepository;

}
