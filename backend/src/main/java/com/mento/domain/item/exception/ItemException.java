package com.mento.domain.item.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class ItemException extends BusinessException {
	public ItemException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
