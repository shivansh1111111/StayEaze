package com.business.booking.service;

import com.business.booking.database.entities.Place;
import com.business.booking.dto.PlaceAutoSearchResponse;
import com.business.booking.dto.PlaceDto;
import com.business.booking.dto.PlacesListDto;

public interface PlaceService {
    public PlacesListDto getPlaces();
    public PlaceDto getPlace(String placeId);
    public PlacesListDto getUserPlaces(String userId);
    public PlaceDto updatePlace(PlaceDto placeDto);
    public PlaceDto createPlace(PlaceDto placeDto);
    public PlaceAutoSearchResponse getAllPlacesAutocomplete(String prefixKeyword);
}
