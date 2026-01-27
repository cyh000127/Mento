package com.mento.domain.timetable.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mento.domain.timetable.converter.TimetableConverter;
import com.mento.domain.timetable.dto.response.MonthlyTimetableResDto;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.vo.DateRange;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableFacadeService {
	private final TimetableQueryService timetableQueryService;

	public MonthlyTimetableResDto getMonthlyTimetables(final LocalDate baseDate) {
		LocalDate searchDate = baseDate != null ? baseDate : LocalDate.now();
		DateRange dateRange = DateRange.of(searchDate, searchDate.plusDays(30));
		List<Timetable> timetables = timetableQueryService.findAllByDateRange(dateRange);
		return TimetableConverter.toMonthlyTimetableResDto(dateRange, timetables);
	}
}