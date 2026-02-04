package com.mento.common.error.exception;

import com.mento.common.error.ErrorCode;

public class AiException extends BusinessException {
	public AiException(final ErrorCode errorCode) {
		super(errorCode);
	}
}