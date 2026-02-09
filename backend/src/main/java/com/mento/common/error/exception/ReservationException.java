package com.mento.common.error.exception;

import com.mento.common.error.ErrorCode;

import lombok.Getter;

@Getter
public class ReservationException extends BusinessException {
	public ReservationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
