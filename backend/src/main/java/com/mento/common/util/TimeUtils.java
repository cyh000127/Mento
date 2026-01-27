package com.mento.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtils {

	private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Seoul");
	private static final int KOREA_UTC_OFFSET_HOURS = 9;

	public LocalDateTime nowAsLocalDateTime() {
		return LocalDateTime.now(DEFAULT_ZONE_ID);
	}

	public LocalDate nowAsLocalDate() {
		return LocalDate.now(DEFAULT_ZONE_ID);
	}

	public LocalTime toKoreanLocalTime(LocalTime localTime) {
		return localTime.plusHours(KOREA_UTC_OFFSET_HOURS);
	}

}
