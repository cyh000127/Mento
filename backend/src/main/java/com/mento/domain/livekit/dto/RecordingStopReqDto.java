package com.mento.domain.livekit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Schema(description = "녹화 중지 요청 DTO")
@Builder
public record RecordingStopReqDto(
	@Schema(description = "Egress ID (녹화 작업 ID)", example = "EG_abc123xyz", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "egressId는 필수입니다")
	String egressId
) {
}
