package com.mento.domain.timetable.exceptioon;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;

public class TimeTableException extends BusinessException {
	public TimeTableException(final ErrorCode errorCode) {
		super(errorCode);
	}
}
