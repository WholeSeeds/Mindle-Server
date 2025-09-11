package com.wholeseeds.mindle.domain.place.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class PlaceNameRequiredException extends BusinessException {
	public PlaceNameRequiredException() {
		super(ErrorCode.PLACE_NAME_REQUIRED);
	}
}
