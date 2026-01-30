package com.mento.domain.reservation.dto.response;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.mento.domain.mentor.dto.common.MentoTypeInfoDto;
import com.mento.domain.reservation.entity.ReservationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReservationPageInfoDto(
	@Schema(description = "예약 ID")
	Long reservationId,

	@Schema(description = "상담 일자", example = "2025-01-29")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate scheduledDate,

	@Schema(description = "상담 유형")
	MentoTypeInfoDto mentorType,

	@Schema(description = "예약 상태")
	ReservationStatus status
) {
}
