package com.wholeseeds.mindle.domain.location.dto;

import com.wholeseeds.mindle.domain.location.entity.Subdistrict;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SubdistrictDto {
	private Long id;
	private String name;
	private Subdistrict.Type type;
}
