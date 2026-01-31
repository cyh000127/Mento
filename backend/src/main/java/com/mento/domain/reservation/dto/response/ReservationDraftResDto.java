package com.mento.domain.reservation.dto.response;

import java.time.LocalDateTime;

import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.timetable.dto.response.common.TimetableSlotInfoDto;

import lombok.Builder;

@Builder
public record ReservationDraftResDto(
	Long reservationId,
	TimetableSlotInfoDto timetableSlotInfoDto,
	ReservationStatus status,
	LocalDateTime expiresAt
) {
}
