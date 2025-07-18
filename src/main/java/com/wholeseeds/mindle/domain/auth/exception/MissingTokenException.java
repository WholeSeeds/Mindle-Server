package com.wholeseeds.mindle.domain.auth.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class MissingTokenException extends BusinessException {

	public MissingTokenException() {
		super(ErrorCode.MISSING_TOKEN);
	}
}
