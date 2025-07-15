package com.wholeseeds.mindle.domain.auth.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class InvalidTokenException extends BusinessException {

	public InvalidTokenException() {
		super(ErrorCode.INVALID_TOKEN);
	}
}
