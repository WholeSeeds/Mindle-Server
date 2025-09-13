package com.wholeseeds.mindle.domain.region.dto.response;

import java.util.List;

import com.wholeseeds.mindle.domain.region.dto.DistrictDto;
import com.wholeseeds.mindle.domain.region.dto.SubdistrictDto;
import com.wholeseeds.mindle.domain.region.enums.RegionType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class RegionDetailResponseDto<T> {

	private final RegionType regionType;
	private final T region;
	private final List<DistrictDto> districts;
	private final List<SubdistrictDto> subdistricts;
}
