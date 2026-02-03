package com.mento.domain.consulting.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class ConsultingException extends BusinessException {
	public ConsultingException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
