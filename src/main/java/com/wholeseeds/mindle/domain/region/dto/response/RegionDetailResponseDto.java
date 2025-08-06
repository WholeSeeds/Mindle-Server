package com.wholeseeds.mindle.domain.region.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class RegionDetailResponseDto<T, U> {
	private T region;
	private List<U> children;
}
