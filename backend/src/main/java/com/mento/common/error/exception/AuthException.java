package com.mento.common.error.exception;

import com.mento.common.error.ErrorCode;

import lombok.Getter;

@Getter
public class AuthException extends BusinessException {
	public AuthException(ErrorCode errorCode) {
		super(errorCode);
	}
}
