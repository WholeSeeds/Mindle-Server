package com.wholeseeds.mindle.domain.region.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "행정구역 이름 기반 조회 요청 DTO")
public class RegionNameRequestDto {

	@NotBlank
	@Schema(description = "시/도 이름 (필수)", example = "수원시")
	private String cityName;

	@Schema(description = "시/군/구 이름 (선택)", example = "영통구")
	private String districtName;

	@Schema(description = "읍/면/동 이름 (선택)", example = "망포1동")
	private String subdistrictName;
}
