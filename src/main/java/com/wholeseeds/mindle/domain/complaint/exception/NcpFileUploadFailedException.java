package com.wholeseeds.mindle.domain.complaint.exception;

import com.wholeseeds.mindle.common.exception.BusinessException;
import com.wholeseeds.mindle.common.response.ErrorCode;

public class NcpFileUploadFailedException extends BusinessException {

	public NcpFileUploadFailedException() {
		super(ErrorCode.NCP_FILE_UPLOAD_FAILED);
	}
}
