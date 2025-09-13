package com.wholeseeds.mindle.domain.region.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class InvalidRegionNameCombinationException extends BusinessException {
	public InvalidRegionNameCombinationException() {
		super(ErrorCode.INVALID_REGION_NAME_COMBINATION);
	}
}
