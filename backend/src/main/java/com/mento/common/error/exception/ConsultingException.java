package com.mento.common.error.exception;

import com.mento.common.error.ErrorCode;

import lombok.Getter;

@Getter
public class ConsultingException extends BusinessException {
	public ConsultingException(ErrorCode errorCode) {
		super(errorCode);
	}
}
