package com.mento.common.error.exception;

import com.mento.common.error.ErrorCode;

public class PaymentException extends BusinessException {
	public PaymentException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
