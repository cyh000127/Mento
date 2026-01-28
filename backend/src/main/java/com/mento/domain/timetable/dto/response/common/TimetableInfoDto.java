package com.mento.domain.timetable.dto.response.common;

import java.time.LocalTime;

import com.mento.domain.timetable.entity.TimetableStatus;

import lombok.Builder;

@Builder
public record TimetableInfoDto(
	Long id,
	LocalTime scheduledTime,
	TimetableStatus status,
	Integer maxCapacity,
	Integer currentCapacity,
	Integer availableCapacity
) {
}
