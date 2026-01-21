package com.mready.domain.member.exception;

import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.BusinessException;

public class MemberException extends BusinessException {
	public MemberException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
