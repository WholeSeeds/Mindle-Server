package com.wholeseeds.mindle.domain.complaint.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class CategoryNotFoundException extends BusinessException {

	public CategoryNotFoundException() {
		super(ErrorCode.CATEGORY_NOT_FOUND);
	}
}
