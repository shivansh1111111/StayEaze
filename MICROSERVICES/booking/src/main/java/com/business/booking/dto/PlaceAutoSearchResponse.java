package com.business.booking.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PlaceAutoSearchResponse extends Response{
    List<PlaceAutoSearchDto> places;
}
