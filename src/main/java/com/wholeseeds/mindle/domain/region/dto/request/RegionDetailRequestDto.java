package com.wholeseeds.mindle.domain.region.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "행정구역 상세 조회 요청 DTO")
public class RegionDetailRequestDto {

	@Schema(description = "행정구역 종류", example = "city", allowableValues = {"city", "district", "subdistrict"})
	private String regionType;

	@Schema(description = "행정구역 코드", example = "11000")
	private String code;
}
