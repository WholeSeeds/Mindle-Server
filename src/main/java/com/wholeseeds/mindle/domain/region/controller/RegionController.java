package com.wholeseeds.mindle.domain.region.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wholeseeds.mindle.common.util.ResponseTemplate;
import com.wholeseeds.mindle.domain.region.dto.request.RegionDetailRequestDto;
import com.wholeseeds.mindle.domain.region.dto.response.RegionDetailResponseDto;
import com.wholeseeds.mindle.domain.region.enums.RegionType;
import com.wholeseeds.mindle.domain.region.service.RegionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "행정구역")
@RestController
@RequestMapping("/api/region")
@RequiredArgsConstructor
public class RegionController {

	private final RegionService regionService;
	private final ResponseTemplate responseTemplate;

	/**
	 * 행정구역 상세 조회 및 하위 목록 조회
	 */
	@PostMapping("/detail")
	@Operation(
		summary = "행정구역 상세 및 하위 목록 조회",
		description = """
		입력받은 regionType(city / district / subdistrict)과 code를 기준으로, 해당 행정구역의 상세 정보 및 하위 목록을 조회합니다.

		city는 시/군, district는 구, subdistrict는 읍/면/동을 의미합니다.

		응답 구조는 모든 regionType에 대해 일관성을 유지합니다:
		- regionType: 요청한 행정구역 타입
		- region: 해당 행정구역 정보 (CityDto / DistrictDto / SubdistrictDto)
		- districts: 하위 구 목록 (city인 경우에만 포함, 이외의 경우는 null)
		- subdistricts: 하위 읍/면/동 목록 (city, district인 경우 포함, 이외의 경우는 null)
		""",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "행정구역 상세 조회 요청",
			required = true,
			content = @Content(schema = @Schema(implementation = RegionDetailRequestDto.class))
		)
	)
	@ApiResponse(
		responseCode = "200",
		description = "행정구역 상세 정보 반환",
		content = @Content(schema = @Schema(implementation = RegionDetailResponseDto.class))
	)
	public ResponseEntity<Map<String, Object>> getRegionDetail(
		@RequestBody RegionDetailRequestDto request
	) {
		RegionType type = RegionType.from(request.getRegionType());
		RegionDetailResponseDto<?> response = regionService.getRegionDetail(type, request.getCode());
		return responseTemplate.success(response, HttpStatus.OK);
	}
}
