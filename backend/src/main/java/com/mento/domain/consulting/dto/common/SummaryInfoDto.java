package com.mento.domain.consulting.dto.common;

import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "상담 보고서 요약 정보")
@Builder
public record SummaryInfoDto(
	@Schema(description = "보고서 ID", example = "1")
	Long reportId,

	@Schema(description = "보고서 내용 (상담 요약, AI 생성 내용)", example = "상담 진행 결과 고객님의 피부 타입은 지성 피부로 확인되었습니다...")
	String content,

	@ArraySchema(
		schema = @Schema(
			description = "보고서 관련 미디어 파일 URL 목록 (영상 녹화본 등)",
			example = "https://example.com/media/report-video.mp4"
		)
	)
	List<String> mediaUrls
) {
}
