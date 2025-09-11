package com.wholeseeds.mindle.domain.moderation.controller;

import static org.springframework.http.MediaType.*;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wholeseeds.mindle.common.annotation.RequireAuth;
import com.wholeseeds.mindle.common.util.ResponseTemplate;
import com.wholeseeds.mindle.domain.moderation.dto.request.ProfanityCheckRequestDto;
import com.wholeseeds.mindle.domain.moderation.dto.response.ProfanityCheckResponseDto;
import com.wholeseeds.mindle.domain.moderation.service.ProfanityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "콘텐츠 검증")
@RestController
@RequestMapping(value = "/api/moderation/profanity", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProfanityController {

	private final ProfanityService profanityService;
	private final ResponseTemplate responseTemplate;

	@Operation(
		summary = "비속어 검사",
		description = "본문 텍스트에서 비속어를 탐지해, 각 비속어와 시작 인덱스(0-base)를 반환합니다. 비속어가 없으면 passed=true로 반환합니다."
	)
	@ApiResponse(
		responseCode = "200",
		description = "검사 결과 반환",
		content = @Content(schema = @Schema(implementation = ProfanityCheckResponseDto.class))
	)
	@PostMapping(value = "/check", consumes = APPLICATION_JSON_VALUE)
	@RequireAuth
	public ResponseEntity<Map<String, Object>> check(@RequestBody @Valid ProfanityCheckRequestDto req) {
		ProfanityCheckResponseDto result = profanityService.check(req);
		return responseTemplate.success(result, HttpStatus.OK);
	}
}
