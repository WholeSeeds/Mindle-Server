package com.wholeseeds.mindle.domain.region.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.wholeseeds.mindle.domain.region.dto.CityDto;
import com.wholeseeds.mindle.domain.region.dto.DistrictDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;
import com.wholeseeds.mindle.domain.region.entity.City;
import com.wholeseeds.mindle.domain.region.entity.District;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;

@Mapper(componentModel = "spring")
public interface RegionMapper {

	CityDto toCityDto(City city);

	@Mapping(source = "city.code", target = "cityCode")
	DistrictDto toDistrictDto(District district);

	SubdistrictDto toSubdistrictDto(Subdistrict subdistrict);

	List<CityDto> toCityDtoList(List<City> cities);

	List<DistrictDto> toDistrictDtoList(List<District> districts);

	List<SubdistrictDto> toSubdistrictDtoList(List<Subdistrict> subdistricts);
}
