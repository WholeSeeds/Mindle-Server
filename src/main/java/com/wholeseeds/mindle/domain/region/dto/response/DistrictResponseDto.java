package com.wholeseeds.mindle.domain.region.dto.response;

import java.util.List;

import com.wholeseeds.mindle.domain.region.dto.DistrictDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DistrictResponseDto extends RegionBaseResponseDto<DistrictDto> {

	private final List<SubdistrictDto> subdistricts;

	@Builder
	public DistrictResponseDto(DistrictDto region, List<SubdistrictDto> subdistricts) {
		super(region);
		this.subdistricts = subdistricts;
	}
}
