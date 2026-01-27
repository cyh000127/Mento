package com.mento.domain.file.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class FileStorageException extends BusinessException {
	public FileStorageException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
