package com.mento.domain.reservation.enums;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReservationStatus {
	IN_PROGRESS("예약 진행중"),
	PENDING_PAYMENT("결제 대기"),
	CONFIRMED("예약 확정"),
	CANCELLED("사용자 취소"),
	EXPIRED("요청 시간 초과"),
	COMPLETED("상담 완료"),
	PAYMENT_FAILED("결제 실패");

	private final String description;

	public static List<ReservationStatus> getActiveStatuses() {
		return List.of(ReservationStatus.PENDING_PAYMENT, ReservationStatus.CONFIRMED);
	}
}
