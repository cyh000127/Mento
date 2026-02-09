package com.mento.domain.livekit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "녹화 응답 DTO")
@Builder
public record RecordingResDto(
	@Schema(description = "Egress ID (녹화 작업 ID)", example = "EG_abc123xyz")
	String egressId,

	@Schema(description = "LiveKit 방 ID", example = "room_abc123")
	String roomId,

	@Schema(description = "녹화 상태", example = "STARTED")
	String status,

	@Schema(description = "상태 메시지", example = "녹화가 시작되었습니다")
	String message
) {
}
