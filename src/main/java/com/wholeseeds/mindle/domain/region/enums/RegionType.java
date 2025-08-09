package com.wholeseeds.mindle.domain.region.enums;

import com.wholeseeds.mindle.domain.region.exception.InvalidRegionTypeException;

import lombok.Getter;

@Getter
public enum RegionType {
	CITY, DISTRICT, SUBDISTRICT;

	public static RegionType from(String value) {
		try {
			return RegionType.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new InvalidRegionTypeException();
		}
	}
}
