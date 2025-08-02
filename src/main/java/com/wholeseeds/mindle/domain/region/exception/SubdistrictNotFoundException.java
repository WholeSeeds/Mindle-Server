package com.wholeseeds.mindle.domain.region.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class SubdistrictNotFoundException extends BusinessException {

	public SubdistrictNotFoundException() {
		super(ErrorCode.SUBDISTRICT_NOT_FOUND);
	}
}
