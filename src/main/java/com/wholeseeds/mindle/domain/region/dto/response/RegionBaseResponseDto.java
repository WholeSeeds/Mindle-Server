package com.wholeseeds.mindle.domain.region.dto.response;

import lombok.Getter;

@Getter
public abstract class RegionBaseResponseDto<T> {
	protected final T region;

	protected RegionBaseResponseDto(T region) {
		this.region = region;
	}
}
