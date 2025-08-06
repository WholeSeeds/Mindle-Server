package com.wholeseeds.mindle.domain.region.dto.response;

import java.util.List;

import com.wholeseeds.mindle.domain.region.dto.CityDto;
import com.wholeseeds.mindle.domain.region.dto.DistrictDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CityResponseDto extends RegionBaseResponseDto<CityDto> {

	private final List<DistrictDto> districts;
	private final List<SubdistrictDto> subdistricts;

	@Builder
	public CityResponseDto(CityDto region, List<DistrictDto> districts, List<SubdistrictDto> subdistricts) {
		super(region);
		this.districts = districts;
		this.subdistricts = subdistricts;
	}
}
