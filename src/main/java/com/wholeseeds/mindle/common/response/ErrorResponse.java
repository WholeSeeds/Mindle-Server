package com.wholeseeds.mindle.common.response;

/**
 * API 에러 응답 레코드 클래스
 *
 * @param status  HTTP 상태 코드
 * @param message 에러 메시지
 */
public record ErrorResponse(int status, String message) {
	public static ErrorResponse of(int status, String message) {
		return new ErrorResponse(status, message);
	}
}
