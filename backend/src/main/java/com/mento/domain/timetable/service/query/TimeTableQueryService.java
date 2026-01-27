package com.mento.domain.timetable.service.query;

import com.mento.domain.timetable.entity.Timetable;

public interface TimeTableQueryService {
	Timetable findByReservationId(final Long id);
}
