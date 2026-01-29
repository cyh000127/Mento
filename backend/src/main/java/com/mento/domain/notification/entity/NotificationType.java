package com.mento.domain.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

	RESERVATION_REMINDER("예약 리마인더", "곧 상담이 시작됩니다.", Path.CONSULTING),
	RESERVATION_CONFIRMED("예약 확정", "상담 예약이 확정되었습니다.", Path.CONSULTING),
	CONSULTING_STARTED("상담 시작", "상담이 시작되었습니다. 입장해주세요.", Path.CONSULTING),
	RESERVATION_CANCELLED("예약 취소", "상담 예약이 취소되었습니다.", Path.NOTIFICATIONS),
	REPORT_READY("리포트 도착", "컨설팅 리포트가 생성되었습니다.", Path.REPORT),
	INVENTORY_EXPIRY("아이템 만료", "만료된 아이템이 존재합니다.", Path.INVENTORY);

	private final String defaultTitle;
	private final String defaultMessage;
	private final String defaultUrl;

	private static class Path {
		private static final String CONSULTING = "/consultations";
		private static final String REPORT = "/mypage/reports";
		private static final String INVENTORY = "/inventories";
		private static final String NOTIFICATIONS = "/notifications";
	}
}
