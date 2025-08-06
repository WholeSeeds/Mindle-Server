package com.wholeseeds.mindle.domain.region.dto.response;

import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;

import lombok.Getter;

@Getter
public class SubdistrictResponseDto extends RegionBaseResponseDto<SubdistrictDto> {

	public SubdistrictResponseDto(SubdistrictDto region) {
		super(region);
	}
}
