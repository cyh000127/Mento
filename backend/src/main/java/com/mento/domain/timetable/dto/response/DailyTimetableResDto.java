package com.mento.domain.timetable.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.mento.domain.timetable.dto.response.common.TimetableInfoDto;

import lombok.Builder;

@Builder
public record DailyTimetableResDto(
	LocalDate date,
	List<TimetableInfoDto> timetables
) {
}
