package com.mento.domain.notification.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class NotificationException extends BusinessException {
	public NotificationException(ErrorCode errorCode) {
		super(errorCode);
	}
}
