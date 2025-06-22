package com.wholeseeds.mindle.common.response;

/**
 * 에러 응답 레코드 클래스
 *
 * @param status  HTTP 상태 코드
 * @param message 에러 메시지
 */
public record ErrorResponse(int status, String message) {
	public static ErrorResponse of(ErrorCode code) {
		return new ErrorResponse(code.getStatus(), code.getDefaultMessage());
	}
}
