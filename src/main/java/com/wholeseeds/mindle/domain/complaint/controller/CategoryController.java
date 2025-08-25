package com.wholeseeds.mindle.domain.complaint.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wholeseeds.mindle.common.annotation.RequireAuth;
import com.wholeseeds.mindle.common.util.ResponseTemplate;
import com.wholeseeds.mindle.domain.complaint.dto.response.CategoryDto;
import com.wholeseeds.mindle.domain.complaint.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "카테고리")
@RestController
@RequestMapping(value = "/api/category", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;
	private final ResponseTemplate responseTemplate;

	/**
	 * 카테고리 전체 조회
	 */
	@Operation(
		summary = "카테고리 전체 조회",
		description = "루트부터 하위까지 계층 구조로 전체 카테고리 목록을 반환합니다."
	)
	@ApiResponse(responseCode = "200", description = "카테고리 전체 반환")
	@GetMapping("/all")
	@RequireAuth
	public ResponseEntity<Map<String, Object>> getCategoryTree() {
		List<CategoryDto> categoryAll = categoryService.getCategoryAll();
		return responseTemplate.success(categoryAll, HttpStatus.OK);
	}
}
