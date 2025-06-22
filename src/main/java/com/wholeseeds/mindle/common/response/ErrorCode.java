package com.wholeseeds.mindle.common.response;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 에러 코드 열거형 클래스
 * 각 에러 코드에 대한 HTTP 상태 코드와 기본 메시지를 정의합니다.
 */
@Getter
public enum ErrorCode {

	BAD_REQUEST(400, "잘못된 요청입니다."),
	UNAUTHORIZED(401, "인증이 필요합니다."),
	FORBIDDEN(403, "접근 권한이 없습니다."),
	NOT_FOUND(404, "존재하지 않는 리소스입니다."),
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류입니다.");

	private final int status;
	private final String defaultMessage;

	ErrorCode(int status, String defaultMessage) {
		this.status = status;
		this.defaultMessage = defaultMessage;
	}

	public HttpStatus toHttpStatus() {
		return HttpStatus.valueOf(status);
	}
}
