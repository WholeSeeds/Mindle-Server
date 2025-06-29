package com.wholeseeds.mindle.common.response;

/**
 * API 응답 레코드 클래스
 *
 * @param status HTTP 상태 코드
 * @param data   응답 데이터
 * @param <T>    데이터 타입
 */
public record ApiResponse<T>(int status, T data) {
	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(200, data);
	}
}
