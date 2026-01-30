package com.mento.domain.mentor.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class MentorException extends BusinessException {
	public MentorException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
