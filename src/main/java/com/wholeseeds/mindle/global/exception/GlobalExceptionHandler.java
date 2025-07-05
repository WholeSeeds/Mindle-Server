package com.wholeseeds.mindle.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;
import com.wholeseeds.mindle.common.response.ErrorResponse;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;

/**
 * 전역 예외 처리 클래스
 */
@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	/**
	 * 비즈니스 로직에서 발생한 커스텀 예외 처리
	 */
	@SuppressWarnings("checkstyle:ParameterName")
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
		ErrorCode errorCode = ex.getErrorCode();
		log.warn("[BusinessException] {} - {}", errorCode.name(), ex.getMessage());
		return ResponseEntity
			.status(errorCode.toHttpStatus())
			.body(ErrorResponse.of(errorCode));
	}

	/**
	 * 잘못된 요청 파라미터나 기본 예외 처리
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.warn("[IllegalArgumentException] {}", ex.getMessage(), ex);
		return ResponseEntity
			.badRequest()
			.body(ErrorResponse.of(ErrorCode.BAD_REQUEST));
	}

	/**
	 * 예상하지 못한 예외 (catch 되지 않은 나머지)
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("[Unhandled Exception]", ex);
		return ResponseEntity
			.internalServerError()
			.body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}
