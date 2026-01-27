package com.mento.domain.timetable.vo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DateRange {

	private final LocalDate startDate;
	private final LocalDate endDate;

	public static DateRange of(final LocalDate startDate, final LocalDate endDate) {
		return new DateRange(startDate, endDate);
	}

	public static DateRange ofOneMonthFromToday(final LocalDate today) {
		LocalDate oneMonthLater = today.plusMonths(1);
		return new DateRange(today, oneMonthLater);
	}

	public List<LocalDate> getAllDates() {
		return startDate.datesUntil(endDate.plusDays(1))
			.toList();
	}

	public boolean contains(final LocalDate date) {
		return !date.isBefore(startDate) && !date.isAfter(endDate);
	}

	public long getDayCount() {
		return ChronoUnit.DAYS.between(startDate, endDate) + 1;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		DateRange dateRange = (DateRange)obj;
		return Objects.equals(startDate, dateRange.startDate) && Objects.equals(endDate, dateRange.endDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(startDate, endDate);
	}

	@Override
	public String toString() {
		return String.format("DateRange{%s ~ %s}", startDate, endDate);
	}
}
