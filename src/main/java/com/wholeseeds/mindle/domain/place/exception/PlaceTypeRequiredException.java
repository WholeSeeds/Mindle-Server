package com.wholeseeds.mindle.domain.place.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class PlaceTypeRequiredException extends BusinessException {
	public PlaceTypeRequiredException() {
		super(ErrorCode.PLACE_TYPE_REQUIRED);
	}
}
