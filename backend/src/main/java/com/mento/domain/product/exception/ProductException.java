package com.mento.domain.product.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class ProductException extends BusinessException {

	public ProductException(ErrorCode errorCode) {
		super(errorCode);
	}
}
