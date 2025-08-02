package com.wholeseeds.mindle.domain.region.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubdistrictType {
	EUP("읍"),
	MYEON("면"),
	DONG("동");

	private final String displayName;
}
