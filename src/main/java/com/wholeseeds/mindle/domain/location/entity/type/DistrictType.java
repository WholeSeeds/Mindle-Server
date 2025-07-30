package com.wholeseeds.mindle.domain.location.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DistrictType {
	GU("구");

	private final String displayName;
}
