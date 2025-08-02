package com.wholeseeds.mindle.domain.region.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class InvalidSubdistrictReferenceException extends BusinessException {

	public InvalidSubdistrictReferenceException() {
		super(ErrorCode.INVALID_SUBDISTRICT_REFERENCE);
	}
}
