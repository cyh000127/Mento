package com.mento.domain.skinanalysis.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "피부 분석 요약 응답 DTO")

public record SkinAnalysisSummaryResDto(

	@Schema(description = "피부 분석 ID", example = "1")
	Long id,

	@JsonProperty("created_at")
	@Schema(description = "분석 일시", example = "2026-02-04T12:00:00")
	LocalDateTime createdAt,

	@JsonProperty("total_score")
	@Schema(description = "종합 점수", example = "85")
	Integer totalScore,

	@JsonProperty("skin_type_summary")
	@Schema(description = "피부 타입 요약", example = "건성")
	String skinTypeSummary
) {

}
