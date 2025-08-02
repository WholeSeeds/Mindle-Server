package com.wholeseeds.mindle.domain.region.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class DistrictNotFoundException extends BusinessException {

	public DistrictNotFoundException() {
		super(ErrorCode.DISTRICT_NOT_FOUND);
	}
}
