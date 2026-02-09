package com.mento.domain.brand.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class BrandException extends BusinessException {

	public BrandException(ErrorCode errorCode) {
		super(errorCode);
	}
}
