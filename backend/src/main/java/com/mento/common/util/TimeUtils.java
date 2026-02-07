package com.mento.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtils {

	private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Seoul");
	private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

	public LocalDateTime nowAsLocalDateTime() {
		return LocalDateTime.now(DEFAULT_ZONE_ID);
	}

	public LocalDate nowAsLocalDate() {
		return LocalDate.now(DEFAULT_ZONE_ID);
	}

	public LocalDateTime toKoreaTime(final LocalDateTime utcDateTime) {
		if (utcDateTime == null) {
			return null;
		}
		ZonedDateTime utcZoned = utcDateTime.atZone(UTC_ZONE_ID);
		return utcZoned.withZoneSameInstant(DEFAULT_ZONE_ID).toLocalDateTime();
	}

}
