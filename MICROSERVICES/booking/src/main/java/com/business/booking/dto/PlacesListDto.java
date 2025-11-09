package com.business.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PlacesListDto extends Response{
    private List<PlaceDto> places;
}

