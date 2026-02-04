package com.mento.domain.livekit.exception;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class LiveKitException extends BusinessException {

	public LiveKitException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
