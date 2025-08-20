package com.wholeseeds.mindle.domain.place.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.wholeseeds.mindle.domain.place.dto.PlaceDto;
import com.wholeseeds.mindle.domain.place.entity.Place;

@Mapper(componentModel = "spring")
public interface PlaceMapper {

	@Mapping(target = "typeName", source = "type.name")
	@Mapping(target = "subdistrictCode", source = "subdistrict.code")
	PlaceDto toPlaceDto(Place place);
}
