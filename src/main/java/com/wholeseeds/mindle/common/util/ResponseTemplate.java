package com.wholeseeds.mindle.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ResponseTemplate {

	private static final String ANSI_RED = "\033[1;31m";
	private static final String ANSI_BLUE = "\033[1;34m";

	/**
	 * 성공 응답 템플릿
	 */
	public ResponseEntity<Map<String, Object>> success(Object data, HttpStatus status) {
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("statusCode", status.value());
		response.put("data", data);
		return new ResponseEntity<>(response, status);
	}

	/**
	 * 실패 응답 템플릿
	 */
	public ResponseEntity<Map<String, Object>> fail(Exception ex, HttpStatus status) {
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("statusCode", status.value());
		response.put("message", ex.getMessage());

		log.error(
			"\n========== ### 예외 발생 ### ==========\n{}{}{}\n==============================",
			ANSI_RED, ExceptionUtils.getStackTrace(ex), ANSI_BLUE
		);

		return new ResponseEntity<>(response, status);
	}
}
