package com.mento.domain.reservation.dto.common;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReservationSurveyDto(
	@Schema(description = "설문 데이터 목록")
	List<SurveyQuestionAnswerDto> surveys
) {
}