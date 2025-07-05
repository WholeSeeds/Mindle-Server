package com.wholeseeds.mindle.domain.place.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class PlaceNotFoundException extends BusinessException {
	public PlaceNotFoundException() {
		super(ErrorCode.PLACE_NOT_FOUND);
	}
}
