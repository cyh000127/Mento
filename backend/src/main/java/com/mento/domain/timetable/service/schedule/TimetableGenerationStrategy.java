package com.mento.domain.timetable.service.schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.factory.TimetableFactory;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.vo.DateRange;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableGenerationStrategy {

	private final TimetableQueryService queryService;
	private final TimetableFactory timetableFactory;

	public List<Timetable> generateForMissingDates(final DateRange dateRange) {
		List<LocalDate> targetDates = dateRange.getAllDates();
		Set<LocalDate> existingDates = queryService.findExistingDatesInRange(
			dateRange.getStartDate(),
			dateRange.getEndDate()
		);

		List<LocalDate> missingDates = filterMissingDates(targetDates, existingDates);
		if (CollectionUtils.isEmpty(missingDates)) {
			log.info("[Timetable] 생성할 타임테이블 없음 {범위: {}}", dateRange);
			return List.of();
		}

		List<Timetable> timetables = generateTimetablesForDates(missingDates);
		log.info("[Timetable] 타임테이블 생성 {범위: {}, 생성일수: {}, 생성개수: {}}", dateRange, missingDates.size(),
			timetables.size());

		return timetables;
	}

	private List<LocalDate> filterMissingDates(final List<LocalDate> targetDates, final Set<LocalDate> existingDates) {
		return targetDates.stream()
			.filter(date -> !existingDates.contains(date))
			.toList();
	}

	private List<Timetable> generateTimetablesForDates(final List<LocalDate> dates) {
		return dates.stream().flatMap(date -> timetableFactory.createDailyTimetables(date).stream())
			.toList();
	}
}
