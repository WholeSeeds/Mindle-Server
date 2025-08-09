package com.wholeseeds.mindle.domain.region.dto;

import com.wholeseeds.mindle.domain.region.entity.type.CityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CityDto {
	private String code;
	private String name;
	private CityType type;
}
