package com.mento.domain.reservation.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class ReservationException extends BusinessException {
	public ReservationException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
