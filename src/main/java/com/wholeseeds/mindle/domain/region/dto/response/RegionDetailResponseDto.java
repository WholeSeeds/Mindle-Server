package com.wholeseeds.mindle.domain.region.dto.response;

import java.util.List;

import com.wholeseeds.mindle.domain.region.dto.CityDto;
import com.wholeseeds.mindle.domain.region.dto.DistrictDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;
import com.wholeseeds.mindle.domain.region.enums.RegionType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionDetailResponseDto<T> {
	private final RegionType regionType;
	private final T region;
	private final List<DistrictDto> districts;
	private final List<SubdistrictDto> subdistricts;

	// 타입 안정성을 위한 Factory Methods
	/**
	 * City(시/군) 응답 생성
	 * @param city 시/군 정보
	 * @param districts 하위 구 목록
	 * @param subdistricts 하위 읍/면/동 목록
	 * @return City 타입의 RegionDetailResponseDto
	 */
	public static RegionDetailResponseDto<CityDto> forCity(
		CityDto city,
		List<DistrictDto> districts,
		List<SubdistrictDto> subdistricts
	) {
		return RegionDetailResponseDto.<CityDto>builder()
			.regionType(RegionType.CITY)
			.region(city)
			.districts(districts)
			.subdistricts(subdistricts)
			.build();
	}

	/**
	 * District(구) 응답 생성
	 * @param district 구 정보
	 * @param subdistricts 하위 읍/면/동 목록
	 * @return District 타입의 RegionDetailResponseDto
	 */
	public static RegionDetailResponseDto<DistrictDto> forDistrict(
		DistrictDto district,
		List<SubdistrictDto> subdistricts
	) {
		return RegionDetailResponseDto.<DistrictDto>builder()
			.regionType(RegionType.DISTRICT)
			.region(district)
			.districts(null)
			.subdistricts(subdistricts)
			.build();
	}

	/**
	 * Subdistrict(읍/면/동) 응답 생성
	 * @param subdistrict 읍/면/동 정보
	 * @return Subdistrict 타입의 RegionDetailResponseDto
	 */
	public static RegionDetailResponseDto<SubdistrictDto> forSubdistrict(
		SubdistrictDto subdistrict
	) {
		return RegionDetailResponseDto.<SubdistrictDto>builder()
			.regionType(RegionType.SUBDISTRICT)
			.region(subdistrict)
			.districts(null)
			.subdistricts(null)
			.build();
	}
}
