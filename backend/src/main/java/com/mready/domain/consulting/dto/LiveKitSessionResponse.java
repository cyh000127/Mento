package com.mready.domain.consulting.dto;

import java.time.LocalDateTime;

public record LiveKitSessionResponse(
        Long timetableId,
        String roomToken,
        String roomName,
        String livekitUrl,
        String participantRole,
        LocalDateTime enteredAt
) {
    public static LiveKitSessionResponse of(Long timetableId, String roomToken, String roomName, String livekitUrl, String participantRole) {
        return new LiveKitSessionResponse(timetableId, roomToken, roomName, livekitUrl, participantRole, LocalDateTime.now());
    }
}
