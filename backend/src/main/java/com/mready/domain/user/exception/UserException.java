package com.mready.domain.user.exception;

import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.BusinessException;

public class UserException extends BusinessException {
	public UserException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
