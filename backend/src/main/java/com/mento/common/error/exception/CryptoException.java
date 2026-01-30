package com.mento.common.error.exception;

import com.mento.common.error.ErrorCode;

public class CryptoException extends BusinessException {
	public CryptoException(ErrorCode errorCode) {
		super(errorCode);
	}
}
