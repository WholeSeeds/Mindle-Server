package com.wholeseeds.mindle.domain.complaint.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class ComplaintNotFoundException extends BusinessException {

	public ComplaintNotFoundException() {
		super(ErrorCode.COMPLAINT_NOT_FOUND);
	}
}
