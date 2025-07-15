package com.wholeseeds.mindle.domain.member.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class DuplicateNicknameException extends BusinessException {
	public DuplicateNicknameException() {
		super(ErrorCode.DUPLICATE_NICKNAME);
	}
}
