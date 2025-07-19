package com.wholeseeds.mindle.common.util;

import java.time.LocalDateTime;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectUtils {
	/**
	 * 객체가 null이거나 비어있는지 확인
	 */
	public static boolean objectIsNullOrEmpty(Object object) {
		if (object == null) {
			return true;
		}
		if (object instanceof String s) {
			return s.isEmpty(); // Java 16+
		}
		if (object instanceof Iterable<?> iterable) {
			return !iterable.iterator().hasNext();
		}
		if (object instanceof Object[] array) {
			return array.length == 0;
		}
		return false;
	}

	/**
	 * 문자열을 LocalDateTime으로 변환
	 */
	public static LocalDateTime stringToLocalDateTime(String dateTimeString) {
		if (dateTimeString == null || dateTimeString.isEmpty()) {
			return null;
		}
		return LocalDateTime.parse(dateTimeString);
	}
}
