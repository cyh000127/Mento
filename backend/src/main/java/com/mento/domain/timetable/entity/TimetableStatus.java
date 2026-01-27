package com.mento.domain.timetable.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum TimetableStatus {
	ACTIVE, INACTIVE, FULL
}
