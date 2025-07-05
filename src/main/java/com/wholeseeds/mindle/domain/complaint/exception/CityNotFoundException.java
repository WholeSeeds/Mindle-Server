package com.wholeseeds.mindle.domain.complaint.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class CityNotFoundException extends BusinessException {

	public CityNotFoundException() {
		super(ErrorCode.CITY_NOT_FOUND);
	}
}
