package com.wholeseeds.mindle.domain.complaint.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class NotComplaintOwnerException extends BusinessException {
	public NotComplaintOwnerException() {
		super(ErrorCode.NOT_COMPLAINT_OWNER);
	}
}
