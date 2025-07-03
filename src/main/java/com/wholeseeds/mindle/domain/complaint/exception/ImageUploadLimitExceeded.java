package com.wholeseeds.mindle.domain.complaint.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class ImageUploadLimitExceeded extends BusinessException {
	public ImageUploadLimitExceeded() {
		super(ErrorCode.IMAGE_UPLOAD_LIMIT_EXCEEDED);
	}
}
