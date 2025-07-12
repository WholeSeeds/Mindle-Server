package com.wholeseeds.mindle.domain.auth.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class MissingCurrentMemberException extends BusinessException {
	public MissingCurrentMemberException() {
		super(ErrorCode.MISSING_CURRENT_MEMBER);
	}
}
