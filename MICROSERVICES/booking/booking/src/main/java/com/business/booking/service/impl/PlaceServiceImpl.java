package com.business.booking.service.impl;

import com.business.booking.database.RepositoryFactory;
import com.business.booking.database.entities.Place;
import com.business.booking.database.entities.PlacePerk;
import com.business.booking.database.entities.PlacePhoto;
import com.business.booking.dto.PlaceAutoSearchDto;
import com.business.booking.dto.PlaceAutoSearchResponse;
import com.business.booking.dto.PlaceDto;
import com.business.booking.dto.PlacesListDto;
import com.business.booking.service.PlaceService;
import com.business.booking.util.dataclasses.UserContext;
import com.business.booking.util.dataclasses.UserContextHolder;
import com.business.booking.util.datastructures.PlaceTrie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {
    private final RepositoryFactory repositoryFactory;
    private boolean trieInitialized = false;
    @Autowired
    private PlaceTrie placeTrie;

    public PlaceServiceImpl(RepositoryFactory repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    @Override
    public PlacesListDto getPlaces() {
        log.info("Fetching all places from database...");

        List<Place> places = repositoryFactory.getPlaceRepository().findAll();

        if (places.isEmpty()) {
            log.warn("No places found in database.");
            return new PlacesListDto();
        }

        log.info("Fetched {} places from DB.", places.size());

        // Map each Place entity to PlaceDto using streams
        List<PlaceDto> placeDtos = places.stream().map(place -> {
            log.debug("Mapping Place entity to DTO. Place ID: {}", place.getPlaceId());

            return PlaceDto.builder()
                    .id(place.getPlaceId())
                    .owner(place.getOwnerId())
                    .title(place.getTitle())
                    .address(place.getAddress())
                    .photos(
                            place.getPhotos() != null
                                    ? place.getPhotos().stream()
                                    .map(PlacePhoto::getUrl) // extract `url` from each PlacePhoto
                                    .collect(Collectors.toList())
                                    : new ArrayList<>()
                    )
                    .description(place.getDescription())
                    .perks(
                            place.getPerks() != null
                                    ? place.getPerks().stream()
                                    .map(PlacePerk::getPerkName) // extract `perkName` from each PlacePerk
                                    .collect(Collectors.toList())
                                    : new ArrayList<>()
                    )
                    .extraInfo(place.getExtraInfo())
                    .checkIn(place.getCheckIn())
                    .checkOut(place.getCheckOut())
                    .maxGuests(place.getMaxGuests())
                    .price(place.getPrice().intValue())
                    .build();

        }).collect(Collectors.toList());

        PlacesListDto placeListDto = new PlacesListDto();
        placeListDto.setPlaces(placeDtos);

        log.info("Successfully mapped {} places into DTOs.", placeDtos.size());

        return placeListDto;
    }


    @Override
    public PlaceDto getPlace(String placeId) {
        log.info("Fetching all plac using placeId from database...");

        Place place = repositoryFactory.getPlaceRepository().findByPlaceId(placeId).orElseThrow(() -> new RuntimeException("Place not found"));

        log.info("Fetched place from DB.");

        return PlaceDto.builder()
                .id(place.getPlaceId())
                .owner(place.getOwnerId())
                .title(place.getTitle())
                .address(place.getAddress())
                .photos(
                        place.getPhotos() != null
                                ? place.getPhotos().stream()
                                .map(PlacePhoto::getUrl) // extract `url` from each PlacePhoto
                                .collect(Collectors.toList())
                                : new ArrayList<>()
                )
                .description(place.getDescription())
                .perks(
                        place.getPerks() != null
                                ? place.getPerks().stream()
                                .map(PlacePerk::getPerkName) // extract `perkName` from each PlacePerk
                                .collect(Collectors.toList())
                                : new ArrayList<>()
                )
                .extraInfo(place.getExtraInfo())
                .checkIn(place.getCheckIn())
                .checkOut(place.getCheckOut())
                .maxGuests(place.getMaxGuests())
                .price(place.getPrice().intValue())
                .build();
    }

    @Override
    public PlacesListDto getUserPlaces(String userId) {
        log.info("Fetching all places from database...");

        List<Place> places = repositoryFactory.getPlaceRepository().findAllByOwnerId(userId);
        PlacesListDto response = new PlacesListDto();

        if (places.isEmpty()) {
            log.warn("No places found in database.");
            List<PlaceDto> placeDtos = new ArrayList<>();
            response.setPlaces(placeDtos);
            return response;
        }

        log.info("Fetched {} places from DB.", places.size());

        // Map each Place entity to PlaceDto using streams
        List<PlaceDto> placeDtos = places.stream().map(place -> {
            log.debug("Mapping Place entity to DTO. Place ID: {}", place.getPlaceId());

            return PlaceDto.builder()
                    .id(place.getPlaceId())
                    .owner(place.getOwnerId())
                    .title(place.getTitle())
                    .address(place.getAddress())
                    .photos(
                            place.getPhotos() != null
                                    ? place.getPhotos().stream()
                                    .map(PlacePhoto::getUrl) // extract `url` from each PlacePhoto
                                    .collect(Collectors.toList())
                                    : new ArrayList<>()
                    )
                    .description(place.getDescription())
                    .perks(
                            place.getPerks() != null
                                    ? place.getPerks().stream()
                                    .map(PlacePerk::getPerkName) // extract `perkName` from each PlacePerk
                                    .collect(Collectors.toList())
                                    : new ArrayList<>()
                    )
                    .extraInfo(place.getExtraInfo())
                    .checkIn(place.getCheckIn())
                    .checkOut(place.getCheckOut())
                    .maxGuests(place.getMaxGuests())
                    .price(place.getPrice().intValue())
                    .build();

        }).collect(Collectors.toList());

        PlacesListDto placeListDto = new PlacesListDto();
        placeListDto.setPlaces(placeDtos);

        log.info("Successfully mapped {} places into DTOs.", placeDtos.size());

        return placeListDto;
    }

    @Override
    public PlaceDto updatePlace(PlaceDto placeDto) {
        String userIdOfLoggedInUser = UserContextHolder.getUserContext().getUserId();

        Place place = repositoryFactory.getPlaceRepository()
                .findByPlaceId(placeDto.getId())
                .orElseThrow(() -> new RuntimeException("Place not found with id: " + placeDto.getId()));

        place.setPlaceId(placeDto.getId());
        place.setOwnerId(userIdOfLoggedInUser);
        place.setTitle(placeDto.getTitle());
        place.setAddress(place.getAddress());
        place.setDescription(placeDto.getDescription());
        place.setExtraInfo(placeDto.getExtraInfo());
        place.setCheckIn(place.getCheckIn());
        place.setCheckOut(place.getCheckOut());
        place.setMaxGuests(place.getMaxGuests());
        place.setPrice(new BigDecimal(placeDto.getPrice()));

        place.getPhotos().clear();
        place.getPerks().clear();

        for(String photosUrl : placeDto.getPhotos()){
            PlacePhoto photo = new PlacePhoto();
            photo.setUrl(photosUrl);
            photo.setPlace(place);
            place.getPhotos().add(photo);
        }

        for(String perk : placeDto.getPerks()){
            PlacePerk placePerk = new PlacePerk();
            placePerk.setPerkName(perk);
            placePerk.setPlace(place);
            place.getPerks().add(placePerk);
        }

        try {
            log.debug("place object going to save in db: "+place);
            repositoryFactory.getPlaceRepository().save(place);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //updated placeDto in memory
        initializeTrie();

        return placeDto;
    }

    @Override
    public PlaceDto createPlace(PlaceDto placeDto) {
        String userIdOfLoggedInUser = UserContextHolder.getUserContext().getUserId();
        Place place = new Place();
        place.setOwnerId(userIdOfLoggedInUser);
        place.setTitle(placeDto.getTitle());
        place.setAddress(placeDto.getAddress());
        place.setDescription(placeDto.getDescription());
        place.setExtraInfo(placeDto.getExtraInfo());
        place.setCheckIn(placeDto.getCheckIn());
        place.setCheckOut(placeDto.getCheckOut());
        place.setMaxGuests(placeDto.getMaxGuests());
        place.setPrice(new BigDecimal(placeDto.getPrice()));

        List<PlacePhoto> photos = new ArrayList<>();

        for(String photosUrl : placeDto.getPhotos()){
            PlacePhoto photo = new PlacePhoto();
            photo.setUrl(photosUrl);
            photo.setPlace(place);
            photos.add(photo);
        }

        List<PlacePerk> perks = new ArrayList<>();

        for(String perk : placeDto.getPerks()){
            PlacePerk placePerk = new PlacePerk();
            placePerk.setPerkName(perk);
            placePerk.setPlace(place);
            perks.add(placePerk);
        }

        place.setPhotos(photos);
        place.setPerks(perks);

        try {
            log.debug("place object going to save in db: "+place);
            repositoryFactory.getPlaceRepository().save(place);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //added new place in memory
        PlaceAutoSearchDto dto = new PlaceAutoSearchDto(placeDto.getId(), placeDto.getTitle(),  placeDto.getPhotos().getFirst());
        placeTrie.insert(dto);

        return placeDto;
    }

    @Override
    public PlaceAutoSearchResponse getAllPlacesAutocomplete(String prefixKeyword) {
        return searchPlacesUsingTrie(prefixKeyword);
    }

    // Search using Trie (much faster than database query)
    public PlaceAutoSearchResponse searchPlacesUsingTrie(String searchTerm) {
        PlaceAutoSearchResponse response = new PlaceAutoSearchResponse();
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            response.setPlaces(Collections.emptyList());
            return response;
        }

        List<PlaceAutoSearchDto> placeList = placeTrie.searchWithPrefix(searchTerm)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
        response.setPlaces(placeList);
        return response;
    }

    // Refresh Trie when data changes
    public void refreshTrie() {
        placeTrie.clear();
        initializeTrie();
    }

    @PostConstruct
    public void initializeTrie() {

        List<Object[]> results = repositoryFactory.getPlaceRepository().findAllForAutorsearch();
        List<PlaceAutoSearchDto> placeListDto = results.stream()
                .map(row -> new PlaceAutoSearchDto(
                        row[0] != null ? row[0].toString() : null,
                        row[1] != null ? row[1].toString() : null,
                        row[2] != null ? row[2].toString() : null))
                .collect(Collectors.toList());

        for (PlaceAutoSearchDto dto : placeListDto) {
            placeTrie.insert(dto);
        }
        trieInitialized = true;
        System.out.println("Trie initialized with " + placeListDto.size() + " places");
    }

}
