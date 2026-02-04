package com.mento.domain.skin_analysis.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SkinAnalysisSummaryResDto(
	Long id,

	@JsonProperty("created_at")
	LocalDateTime createdAt,

	@JsonProperty("total_score")
	Integer totalScore,

	@JsonProperty("skin_type_summary")
	String skinTypeSummary
) {
}