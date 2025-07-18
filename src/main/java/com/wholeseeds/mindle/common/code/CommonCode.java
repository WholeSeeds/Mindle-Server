package com.wholeseeds.mindle.common.code;

import java.time.LocalDateTime;

public class CommonCode {
	public static boolean objectIsNullOrEmpty(Object object) {
		if (object == null) {
			return true;
		}
		if (object instanceof String) {
			return ((String)object).isEmpty();
		}
		if (object instanceof Iterable) {
			return !((Iterable<?>)object).iterator().hasNext();
		}
		if (object instanceof Object[]) {
			return ((Object[])object).length == 0;
		}
		return false;
	}

	public static LocalDateTime stringToLocalDateTime(String dateTimeString) {
		if (dateTimeString == null || dateTimeString.isEmpty()) {
			return null;
		}
		return LocalDateTime.parse(dateTimeString);
	}
}
