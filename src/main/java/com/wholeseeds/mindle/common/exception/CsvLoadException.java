package com.wholeseeds.mindle.common.exception;

import com.wholeseeds.mindle.common.response.ErrorCode;

public class CsvLoadException extends BusinessException {

	public CsvLoadException(String customMessage) {
		super(ErrorCode.CSV_LOAD_FAILED, customMessage);
	}
}
