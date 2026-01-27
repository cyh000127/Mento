package com.mento.common.error.exception;

import com.mento.common.error.ErrorCode;

public class FileStorageException extends BusinessException {
	public FileStorageException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
