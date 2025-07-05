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
	QUERYDSL_NOT_INITIALIZED(500, "QueryDSL 필드가 초기화되지 않았습니다."),

	//complaint
	CATEGORY_NOT_FOUND(404, "해당 카테고리를 찾을 수 없습니다"),
	CITY_NOT_FOUND(404, "해당 도시를 찾을 수 없습니다"),
	DISTRICT_NOT_FOUND(404, "해당 구를 찾을 수 없습니다"),
	SUBDISTRICT_NOT_FOUND(404, "해당 (읍/면/동/리)를 찾을 수 없습니다"),
	PLACE_NOT_FOUND(404, "해당 장소를 찾을 수 없습니다"),
	IMAGE_UPLOAD_LIMIT_EXCEEDED(400, "이미지는 최대 3장까지 업로드 가능합니다"),

	// NCP
	NCP_FILE_UPLOAD_FAILED(500, "NCP 에 파일 저장 중 오류가 발생했습니다"),

	;
	private final int status;
	private final String message;

	public HttpStatus toHttpStatus() {
		return HttpStatus.valueOf(status);
	}
}
