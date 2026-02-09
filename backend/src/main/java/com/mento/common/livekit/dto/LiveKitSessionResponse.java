package com.mento.common.livekit.dto;

import java.time.LocalDateTime;

import com.mento.common.util.TimeUtils;

public record LiveKitSessionResponse(
	Long timetableId,
	String roomToken,
	String roomName,
	String livekitUrl,
	String participantRole,
	LocalDateTime enteredAt
) {
	public static LiveKitSessionResponse of(
		Long timetableId,
		String roomToken,
		String roomName,
		String livekitUrl,
		String participantRole
	) {
		return new LiveKitSessionResponse(timetableId, roomToken, roomName, livekitUrl, participantRole,
			TimeUtils.nowAsLocalDateTime());
	}
}
