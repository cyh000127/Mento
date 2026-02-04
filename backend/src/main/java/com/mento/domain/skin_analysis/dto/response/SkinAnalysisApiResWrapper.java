package com.mento.domain.skin_analysis.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SkinAnalysisApiResWrapper(
	@JsonProperty("status_code")
	Integer statusCode,

	String message,

	SkinAnalysisDetailResDto data
) {
}