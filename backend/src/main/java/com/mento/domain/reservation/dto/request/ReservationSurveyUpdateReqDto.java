package com.mento.domain.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ReservationSurveyUpdateReqDto(
	@NotBlank(message = "설문 데이터는 필수입니다")
	String surveyData
) {
}
