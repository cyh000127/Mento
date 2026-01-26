package com.mready.domain.file.exception;

import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.BusinessException;

public class FileStorageException extends BusinessException {
	public FileStorageException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
