package com.mento.domain.livekit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Schema(description = "녹화 중지 요청 DTO")
@Builder
public record RecordingStopReqDto(
	@Schema(description = "room ID", example = "room_345", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "방 번호는 필수입니다")
	String roomId
) {
}
