package com.mento.domain.consulting.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Schema(description = "상담 보고서 요약 정보 DTO")
@Builder
public record SummaryInfoDto(
	@Schema(description = "보고서 ID", example = "1")
	@NotNull(message = "보고서 ID는 필수입니다")
	Long reportId,

	@Schema(description = "보고서 내용")
	@NotBlank(message = "내용은 필수입니다")
	String content,

	@Schema(description = "미디어 URL", example = "https://example.com/media/report.mp4")
	String mediaUrl
) {
}
