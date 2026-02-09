package com.mento.domain.user.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class UserException extends BusinessException {
	public UserException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
