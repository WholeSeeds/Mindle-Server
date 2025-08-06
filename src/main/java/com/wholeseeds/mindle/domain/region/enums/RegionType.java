package com.wholeseeds.mindle.domain.region.enums;

import lombok.Getter;

@Getter
public enum RegionType {
	CITY, DISTRICT, SUBDISTRICT;

	public static RegionType from(String value) {
		try {
			return RegionType.valueOf(value.trim().toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("올바르지 않은 regionType 값입니다: " + value);
		}
	}
}
