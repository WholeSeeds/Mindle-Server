package com.wholeseeds.mindle.domain.location.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CityType {
	SI("시"),
	GOON("군");

	private final String displayName;
}
