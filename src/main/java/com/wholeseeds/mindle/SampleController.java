package com.wholeseeds.mindle;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "샘플컨트롤러2", description = "책 추천 / 지난달 키워드")
public class SampleController {

	@Operation(summary = "책 추천", description = "특정 책 코드를 입력으로 받아 해당 책 기반 추천 책 list를 반환합니다.",
		parameters = {
			@Parameter(name = "weekMonth", description = "'week' 또는 'month'"),
			@Parameter(name = "peerAge", description = "age +- 2 범위의 나이로 또래 인기 대출 책을 조회"),
			@Parameter(name = "ageRange", description = "연령대 코드 (0, 6, 8, 14, 20, 30, 40, 50, 60)"),
			@Parameter(name = "gender", description = "'man' 또는 'woman'"),
		},
		responses = {
			@ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = SampleDto.class)),
				description = SampleDto.description)})
	@GetMapping("/hello")
	public String hello() {
		return "Hello Swagger!";
	}
}
