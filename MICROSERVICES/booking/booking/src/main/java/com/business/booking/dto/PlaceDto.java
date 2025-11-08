package com.business.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlaceDto extends Response{

    @JsonProperty("_id")
    private String id;

    private String owner;
    private String title;
    private String address;
    private List<String> photos;
    private String description;
    private List<String> perks;
    private String extraInfo;
    private int checkIn;
    private int checkOut;
    private int maxGuests;
    private int price;
}

