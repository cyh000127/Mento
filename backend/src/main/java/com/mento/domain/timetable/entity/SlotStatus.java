package com.mento.domain.timetable.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SlotStatus {
	AVAILABLE("예약 가능"),
	FULL("만석"),
	CLOSED("운영 종료");

	private final String description;
}
