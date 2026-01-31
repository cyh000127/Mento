package com.mento.domain.reservation.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.mento.domain.reservation.enums.ReservationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReservationHistoryReqDto(
	@Schema(description = "조회 시작 날짜", example = "2025-01-01")
	@NotNull(message = "시작 날짜는 필수입니다")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate startDate,

	@Schema(description = "조회 종료 날짜", example = "2025-01-31")
	@NotNull(message = "종료 날짜는 필수입니다")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate endDate,

	@Schema(description = "예약 상태 필터링 (선택)", example = "CONFIRMED")
	ReservationStatus status
) {
}
