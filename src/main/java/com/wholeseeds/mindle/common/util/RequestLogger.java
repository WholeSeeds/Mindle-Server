package com.wholeseeds.mindle.common.util;

import java.lang.reflect.Field;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLogger {

	private static final Logger logger = LoggerFactory.getLogger(RequestLogger.class);
	private static final String PREFIX_PARAM = "RequestParam : ";
	private static final String PREFIX_BODY = "RequestBody : ";

	private RequestLogger() {
		// 인스턴스화 방지
	}

	/**
	 * 요청 파라미터를 로깅합니다.
	 * @param keys 파라미터 키 배열
	 * @param params 파라미터 값 배열
	 */
	public static void param(String[] keys, Object... params) {
		StringBuilder sb = new StringBuilder(PREFIX_PARAM).append("{");

		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			Object value = (params != null && i < params.length) ? params[i] : null;

			sb.append(key).append(": ").append(value != null ? value : "null");
			if (i < keys.length - 1) {
				sb.append(", ");
			}
		}

		sb.append("}");
		if (logger.isTraceEnabled()) {
			logger.trace(sb.toString());
		}
	}

	/**
	 * DTO 객체의 필드를 로깅합니다.
	 * @param dto DTO 객체
	 */
	public static void body(Object dto) {
		if (dto == null) {
			logger.trace(PREFIX_BODY + "null");
			return;
		}

		StringBuilder sb = new StringBuilder(PREFIX_BODY);
		Class<?> clazz = dto.getClass();
		String dtoName = clazz.getSimpleName();
		Field[] fields = clazz.getDeclaredFields();

		sb.append(dtoName).append(" {");

		for (Field field : fields) {
			field.setAccessible(true);  // 필드 접근 허용
			String fieldName = field.getName();

			if ("description".equals(fieldName)) {
				continue;
			}

			try {
				Object value = field.get(dto);
				sb.append(formatField(fieldName, value));
			} catch (IllegalAccessException e) {
				sb.append(fieldName).append(": [ACCESS ERROR], ");
			}
		}

		trimTrailingComma(sb);
		sb.append("}");
		if (logger.isTraceEnabled()) {
			logger.trace(sb.toString());
		}
	}

	/**
	 * 필드를 포맷팅합니다.
	 * @param fieldName 필드 이름
	 * @param value 필드 값
	 * @return 포맷된 문자열
	 */
	private static String formatField(String fieldName, Object value) {
		if (value instanceof List<?>) {
			return formatListField(fieldName, (List<?>) value);
		}
		return fieldName + ": " + (value != null ? value : "null") + ", ";
	}

	/**
	 * List 필드를 포맷팅합니다.
	 * @param fieldName 필드 이름
	 * @param list List 객체
	 * @return 포맷된 문자열
	 */
	private static String formatListField(String fieldName, List<?> list) {
		StringBuilder listBuilder = new StringBuilder(fieldName + ": [");

		for (Object item : list) {
			listBuilder.append(item).append(", ");
		}
		if (!list.isEmpty()) {
			listBuilder.setLength(listBuilder.length() - 2); // remove last comma
		}

		listBuilder.append("], ");
		return listBuilder.toString();
	}

	/**
	 * 문자열의 끝에서 쉼표와 공백을 제거합니다.
	 * @param sb 문자열 빌더
	 */
	private static void trimTrailingComma(StringBuilder sb) {
		int len = sb.length();
		if (len >= 2 && sb.charAt(len - 2) == ',' && sb.charAt(len - 1) == ' ') {
			sb.setLength(len - 2);
		}
	}
}
