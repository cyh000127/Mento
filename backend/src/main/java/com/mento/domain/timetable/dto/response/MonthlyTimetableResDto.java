package com.mento.domain.timetable.dto.response;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;

@Builder
public record MonthlyTimetableResDto(
	LocalDate startDate,
	LocalDate endDate,
	Integer totalDays,
	List<DailyTimetableResDto> dailyTimetables
) {
}
