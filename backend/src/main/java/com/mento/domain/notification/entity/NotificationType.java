package com.mento.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

	RESERVATION_REMINDER("예약 리마인더"),
	RESERVATION_CONFIRMED("예약 확정"),
	CONSULTING_STARTED("상담 시작"),
	RESERVATION_CANCELLED("예약 취소"),
	REPORT_READY("리포트 도착"),
	INVENTORY_EXPIRY("아이템 만료");

	private final String description;
}
