package com.business.booking.controllers;

import com.business.booking.dto.PlaceDto;
import com.business.booking.dto.Response;
import com.business.booking.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/businessCompany")
@Slf4j
public class PlaceController {
    @Autowired
    PlaceService placeService;
    @GetMapping(path = "/places")
    public ResponseEntity<Response> getAllPlaces(){
        log.info("Inside getAllPlaces controller method");
        return new ResponseEntity<>(placeService.getPlaces(), HttpStatus.OK);
    }
    @GetMapping(path = "/places/{placeId}")
    public ResponseEntity<Response> getPlace(@PathVariable String placeId){
        return new ResponseEntity<>(placeService.getPlace(placeId), HttpStatus.OK);
    }
    @PostMapping(path = "/places")
    public ResponseEntity<Response> createPlace(@RequestBody PlaceDto placeDto){
        return new ResponseEntity<>(placeService.createPlace(placeDto), HttpStatus.OK);
    }
    @PutMapping(path = "/places")
    public ResponseEntity<Response> updatePlace(@RequestBody PlaceDto placeDto){
        return new ResponseEntity<>(placeService.updatePlace(placeDto), HttpStatus.OK);
    }
    @GetMapping(path = "/user-places")
    public ResponseEntity<Response> getAllPlaces(@RequestParam("userId") String userId){
        return new ResponseEntity<>(placeService.getUserPlaces(userId), HttpStatus.OK);
    }
    @GetMapping(path = "/places/autocomplete")
    public ResponseEntity<Response> getAllPlacesAutocomplete(@RequestParam("keyword") String keyword){
        return new ResponseEntity<>(placeService.getAllPlacesAutocomplete(keyword), HttpStatus.OK);
    }

}
