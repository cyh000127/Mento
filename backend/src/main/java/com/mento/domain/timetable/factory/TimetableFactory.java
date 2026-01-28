package com.mento.domain.timetable.factory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.stereotype.Component;

import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TimetableFactory {

	private static final int START_HOUR = 9;
	private static final int END_HOUR = 17;
	private static final int DEFAULT_MAX_CAPACITY = 15;
	private static final int DEFAULT_CURRENT_CAPACITY = 0;
	private static final int MINUTE = 0;

	public Timetable createTimetable(final LocalDate scheduledDate, final LocalTime scheduledTime) {
		return Timetable.builder()
			.scheduledDate(scheduledDate)
			.scheduledTime(scheduledTime)
			.status(TimetableStatus.ACTIVE)
			.maxCapacity(DEFAULT_MAX_CAPACITY)
			.currentCapacity(DEFAULT_CURRENT_CAPACITY)
			.build();
	}

	public List<Timetable> createDailyTimetables(final LocalDate scheduledDate) {
		return IntStream.rangeClosed(START_HOUR, END_HOUR)
			.mapToObj(hour -> createTimetable(scheduledDate, LocalTime.of(hour, MINUTE)))
			.toList();
	}
}
