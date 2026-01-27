package com.mento.domain.timetable.converter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mento.domain.timetable.dto.response.DailyTimetableResDto;
import com.mento.domain.timetable.dto.response.MonthlyTimetableResDto;
import com.mento.domain.timetable.dto.response.common.TimetableInfoDto;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.vo.DateRange;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimetableConverter {

	public TimetableInfoDto toTimetableInfoResDto(final Timetable timetable) {
		return TimetableInfoDto.builder()
			.id(timetable.getId())
			.scheduledTime(timetable.getScheduledTime())
			.status(timetable.getStatus())
			.maxCapacity(timetable.getMaxCapacity())
			.currentCapacity(timetable.getCurrentCapacity())
			.availableCapacity(timetable.getMaxCapacity() - timetable.getCurrentCapacity())
			.build();
	}

	public DailyTimetableResDto toDailyTimetableResDto(final LocalDate date, final List<Timetable> timetables) {
		List<TimetableInfoDto> timetableInfos = timetables.stream()
			.map(TimetableConverter::toTimetableInfoResDto)
			.toList();

		return DailyTimetableResDto.builder()
			.date(date)
			.timetables(timetableInfos)
			.build();
	}

	public MonthlyTimetableResDto toMonthlyTimetableResDto(
		final DateRange dateRange,
		final List<Timetable> timetables
	) {
		Map<LocalDate, List<Timetable>> timetablesByDate = timetables.stream()
			.collect(Collectors.groupingBy(Timetable::getScheduledDate));

		List<DailyTimetableResDto> dailyTimetables = dateRange.getAllDates().stream()
			.map(date -> toDailyTimetableResDto(date, timetablesByDate.getOrDefault(date, List.of())))
			.toList();

		return MonthlyTimetableResDto.builder()
			.startDate(dateRange.getStartDate())
			.endDate(dateRange.getEndDate())
			.totalDays((int)dateRange.getDayCount())
			.dailyTimetables(dailyTimetables)
			.build();
	}
}
