package com.mento.domain.timetable.service.query;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.mento.domain.timetable.entity.Timetable;

public interface TimetableQueryService {
	Timetable findByReservationId(final Long id);

	List<Timetable> findAllExpiredTimetables(final LocalDate now);

	Set<LocalDate> findExistingDatesInRange(final LocalDate startDate, final LocalDate endDate);

	boolean existsByScheduledDate(LocalDate scheduledDate);
}
