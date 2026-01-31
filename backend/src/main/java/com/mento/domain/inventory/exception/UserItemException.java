package com.mento.domain.inventory.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class UserItemException extends BusinessException {
	public UserItemException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
