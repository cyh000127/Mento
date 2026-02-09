package com.mento.domain.livekit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Schema(description = "녹화 시작 요청 DTO")
@Builder
public record RecordingReqDto(
	@Schema(description = "LiveKit 방 ID", example = "room_abc123", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "roomId는 필수입니다")
	String roomId,

	@Schema(description = "멘토 ID (선택)", example = "mentor_123")
	String mentorId,

	@Schema(description = "오디오 트랙 SID", example = "TR_audio123", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "audioTrackSid는 필수입니다")
	String audioTrackSid,

	@Schema(description = "비디오 트랙 SID", example = "TR_video456", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "videoTrackSid는 필수입니다")
	String videoTrackSid
) {
}
