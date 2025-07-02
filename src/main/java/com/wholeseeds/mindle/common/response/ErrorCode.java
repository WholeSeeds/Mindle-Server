package com.wholeseeds.mindle.common.response;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 열거형 클래스
 * 각 에러 코드에 대한 HTTP 상태 코드와 기본 메시지를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// global
	BAD_REQUEST(400, "잘못된 요청입니다."),
	UNAUTHORIZED(401, "인증이 필요합니다."),
	FORBIDDEN(403, "접근 권한이 없습니다."),
	NOT_FOUND(404, "존재하지 않는 리소스입니다."),
	INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

	// member
	MEMBER_NOT_FOUND(404, "해당 회원을 찾을 수 없습니다."),

	// develop
	QUERYDSL_NOT_INITIALIZED(500, "QueryDSL 필드가 초기화되지 않았습니다.");

	private final int status;
	private final String message;

	public HttpStatus toHttpStatus() {
		return HttpStatus.valueOf(status);
	}
}
