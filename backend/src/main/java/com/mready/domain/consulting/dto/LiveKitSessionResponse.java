package com.mready.domain.consulting.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LiveKitSessionResponse {
    private Long timetableId;
    private String roomToken;
    private String roomName;
    private String livekitUrl;
    private String participantRole;
    private LocalDateTime enteredAt;

}
