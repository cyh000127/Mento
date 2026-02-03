package com.mento.domain.skin_analysis.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record SkinAnalysisResDto(
	@JsonProperty("total_score")
	Integer totalScore,

	@JsonProperty("total_grade")
	Integer totalGrade,

	@JsonProperty("skin_type_summary")
	String skinTypeSummary,

	SkinDetails details
) {
	public record SkinDetails(
		Metric moisture,
		Metric pore,
		Metric wrinkle,
		Metric pigmentation,
		Metric sagging
	) {
	}

	public record Metric(
		Integer score,
		Integer grade,
		@JsonProperty("raw_value") Double rawValue,
		String description
	) {
	}
}