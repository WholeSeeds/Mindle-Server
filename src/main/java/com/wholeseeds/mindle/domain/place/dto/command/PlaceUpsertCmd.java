package com.wholeseeds.mindle.domain.place.dto.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceUpsertCmd {
	private final String placeId;
	private final String placeTypeName;
	private final String placeName;
	private final String description;
	private final Double latitude;
	private final Double longitude;
	private final String subdistrictCode;
}
