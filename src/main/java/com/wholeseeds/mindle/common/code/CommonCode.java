package com.wholeseeds.mindle.common.code;

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
}
