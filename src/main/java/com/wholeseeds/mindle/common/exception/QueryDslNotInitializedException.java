package com.wholeseeds.mindle.common.exception;

import com.wholeseeds.mindle.common.response.ErrorCode;

public class QueryDslNotInitializedException extends BusinessException {

	public QueryDslNotInitializedException() {
		super(ErrorCode.QUERYDSL_NOT_INITIALIZED);
	}
}
