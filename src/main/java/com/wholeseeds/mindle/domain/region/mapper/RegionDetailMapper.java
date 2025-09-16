package com.wholeseeds.mindle.domain.region.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.wholeseeds.mindle.domain.region.dto.CityDto;
import com.wholeseeds.mindle.domain.region.dto.DistrictDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;
import com.wholeseeds.mindle.domain.region.dto.response.RegionDetailResponseDto;
import com.wholeseeds.mindle.domain.region.entity.City;
import com.wholeseeds.mindle.domain.region.entity.District;
import com.wholeseeds.mindle.domain.region.entity.Subdistrict;
import com.wholeseeds.mindle.domain.region.enums.RegionType;

@Mapper(
	componentModel = "spring",
	uses = { RegionMapper.class },
	imports = { RegionType.class }
)
public interface RegionDetailMapper {

	/**
	 * City 상세 응답 조립 (하위 District/Subdistrict 목록 포함)
	 */
	@Mapping(target = "regionType", expression = "java(RegionType.CITY)")
	@Mapping(target = "region", expression = "java(regionMapper.toCityDto(city))")
	@Mapping(target = "districts", expression = "java(regionMapper.toDistrictDtoList(districts))")
	@Mapping(target = "subdistricts", expression = "java(regionMapper.toSubdistrictDtoList(subdistricts))")
	RegionDetailResponseDto<CityDto> toCityDetail(
		City city,
		List<District> districts,
		List<Subdistrict> subdistricts,
		RegionMapper regionMapper
	);

	/**
	 * District 상세 응답 조립 (하위 Subdistrict 목록 포함)
	 */
	@Mapping(target = "regionType", expression = "java(RegionType.DISTRICT)")
	@Mapping(target = "region", expression = "java(regionMapper.toDistrictDto(district))")
	@Mapping(target = "districts", expression = "java(null)")
	@Mapping(target = "subdistricts", expression = "java(regionMapper.toSubdistrictDtoList(subdistricts))")
	RegionDetailResponseDto<DistrictDto> toDistrictDetail(
		District district,
		List<Subdistrict> subdistricts,
		RegionMapper regionMapper
	);

	/**
	 * Subdistrict 상세 응답 조립
	 */
	@Mapping(target = "regionType", expression = "java(RegionType.SUBDISTRICT)")
	@Mapping(target = "region", expression = "java(regionMapper.toSubdistrictDto(subdistrict))")
	@Mapping(target = "districts", expression = "java(null)")
	@Mapping(target = "subdistricts", expression = "java(null)")
	RegionDetailResponseDto<SubdistrictDto> toSubdistrictDetail(
		Subdistrict subdistrict,
		RegionMapper regionMapper
	);
}
