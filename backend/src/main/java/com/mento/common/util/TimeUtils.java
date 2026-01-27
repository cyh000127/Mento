package com.mento.common.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtils {

	public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Seoul");

	public LocalDateTime nowAsLocalDateTime() {
		return LocalDateTime.now(DEFAULT_ZONE_ID);
	}
}
