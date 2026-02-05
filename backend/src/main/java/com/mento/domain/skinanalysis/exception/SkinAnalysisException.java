package com.mento.domain.skinanalysis.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class SkinAnalysisException extends BusinessException {
	public SkinAnalysisException(final ErrorCode errorCode) {
		super(errorCode);
	}
}