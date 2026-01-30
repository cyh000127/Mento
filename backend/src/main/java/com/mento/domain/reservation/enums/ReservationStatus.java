package com.mento.domain.reservation.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ReservationStatus {
	PENDING_PAYMENT("결제 대기"),
	CONFIRMED("예약 확장"),
	CANCELLED("사용자 취소"),
	EXPIRED("요청 시간 초과"),
	COMPLETED("상담 완료"),
	PAYMENT_FAILED("결제 실패");

	private final String description;
}
