package com.mento.domain.timetable.exceptioon;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class TimetableException extends BusinessException {
	public TimetableException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
