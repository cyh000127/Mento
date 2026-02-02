package com.mento.domain.reservation.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.mento.domain.reservation.enums.ReservationStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
@Schema(description = "예약 이력 조회 요청 DTO")
public record ReservationHistoryReqDto(
	@Schema(description = "조회 시작 날짜 (선택)", example = "2025-01-01")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate startDate,

	@Schema(description = "조회 종료 날짜 (선택)", example = "2025-01-31")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate endDate,

	@Schema(description = "예약 상태 필터링 (선택)", example = "CONFIRMED")
	ReservationStatus status,

	@PositiveOrZero
	@Schema(description = "페이지 번호 (0부터 시작)", defaultValue = "0")
	Integer page,

	@PositiveOrZero
	@Schema(description = "페이지 크기", defaultValue = "10")
	Integer size
) {
}
