package com.wholeseeds.mindle.domain.region.dto;

import com.wholeseeds.mindle.domain.region.entity.type.SubdistrictType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SubdistrictDto {
	private String code;
	private String name;
	private SubdistrictType type;
}
