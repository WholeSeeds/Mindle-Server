package com.wholeseeds.mindle.common.exception;

import com.wholeseeds.mindle.common.response.ErrorCode;

import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {
	private final ErrorCode errorCode;

	protected BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	protected BusinessException(ErrorCode errorCode, String customMessage) {
		super(customMessage);
		this.errorCode = errorCode;
	}
}
