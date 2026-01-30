package com.mento.domain.reservation.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LiveKitConstants {

	public static final String ROOM_NAME_PREFIX = "room_";
	public static final String RESERVATION_DIRECTORY = "reservations/";
	public static final int EARLY_ENTRY_MINUTES = 10;
	public static final int END_MINUTES = 10;

}
