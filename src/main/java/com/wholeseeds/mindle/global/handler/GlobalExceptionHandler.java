package com.wholeseeds.mindle.global.handler;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;
import com.wholeseeds.mindle.common.util.ResponseTemplate;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 전역 예외 처리 클래스
 */
@Hidden
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
	private final ResponseTemplate responseTemplate;

	/**
	 * 비즈니스 로직에서 발생한 커스텀 예외 처리
	 */
	@SuppressWarnings("checkstyle:ParameterName")
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException ex) {
		ErrorCode errorCode = ex.getErrorCode();
		log.warn("[BusinessException] {} - {}", errorCode.name(), ex.getMessage());
		return responseTemplate.fail(ex, errorCode.toHttpStatus());
	}

	/**
	 * 잘못된 요청 파라미터나 기본 예외 처리
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.warn("[IllegalArgumentException] {}", ex.getMessage(), ex);
		return responseTemplate.fail(ex, HttpStatus.BAD_REQUEST);
	}

	/**
	 * 예상하지 못한 예외 (catch 되지 않은 나머지)
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
		log.error("[Unhandled Exception]", ex);
		return responseTemplate.fail(ex, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
