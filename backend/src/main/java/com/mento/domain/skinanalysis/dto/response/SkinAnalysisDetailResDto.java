package com.mento.domain.skinanalysis.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "피부 분석 상세 응답 DTO")
public record SkinAnalysisDetailResDto(
	@JsonProperty("total_score")
	@Schema(description = "종합 점수", example = "85")
	Integer totalScore,

	@JsonProperty("total_grade")
	@Schema(description = "종합 등급 (1-5)", example = "2")
	Integer totalGrade,

	@JsonProperty("skin_type_summary")
	@Schema(description = "피부 타입 요약", example = "건성 및 민감성 피부")
	String skinTypeSummary,

	@Schema(description = "상세 지표")
	SkinDetails details
) {
	@Schema(description = "피부 상세 지표")
	public record SkinDetails(
		@Schema(description = "수분")
		Metric moisture,
		@Schema(description = "모공")
		Metric pore,
		@Schema(description = "주름")
		Metric wrinkle,
		@Schema(description = "색소침착")
		Metric pigmentation,
		@Schema(description = "탄력 저하(처짐)")
		Metric sagging
	) {
	}

	@Schema(description = "개별 지표 상세")
	public record Metric(
		@Schema(description = "점수", example = "80")
		Integer score,
		@Schema(description = "등급 (1-5)", example = "3")
		Integer grade,
		@JsonProperty("raw_value")
		@Schema(description = "실제 측정값", example = "15.5")
		Double rawValue,
		@Schema(description = "지표 설명", example = "수분 수치가 평균보다 높습니다.")
		String description
	) {
	}
}