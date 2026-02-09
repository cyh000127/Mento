package com.mento.domain.reservation.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SurveyQuestionAnswerDto(
	@Schema(description = "질문", example = "피부 고민이 무엇인가요?")
	String question,

	@Schema(description = "응답", example = "건성 피부")
	String answer
) {
}
