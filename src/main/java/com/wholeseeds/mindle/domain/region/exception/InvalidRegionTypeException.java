package com.wholeseeds.mindle.domain.region.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class InvalidRegionTypeException extends BusinessException {

	public InvalidRegionTypeException() {
		super(ErrorCode.INVALID_REGION_TYPE);
	}
}
