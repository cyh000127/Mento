package com.mento.domain.mentor.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class MentortTypeException extends BusinessException {
	public MentortTypeException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
