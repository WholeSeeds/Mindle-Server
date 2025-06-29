package com.wholeseeds.mindle.domain.member.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class MemberNotFoundException extends BusinessException {

	public MemberNotFoundException() {
		super(ErrorCode.MEMBER_NOT_FOUND);
	}
}
