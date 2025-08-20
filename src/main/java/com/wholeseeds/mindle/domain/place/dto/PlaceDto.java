package com.wholeseeds.mindle.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PlaceDto {
	private Long id;
	private String placeId;
	private String name;
	private String description;
	private Double latitude;
	private Double longitude;
	private String typeName;
	private String subdistrictCode;
}
