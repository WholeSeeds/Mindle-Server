package com.wholeseeds.mindle.domain.region.dto;

import com.wholeseeds.mindle.domain.region.entity.type.DistrictType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DistrictDto {
	private String code;
	private String name;
	private DistrictType type;
	private String cityCode;
}
