package com.mento.domain.timetable.service.schedule;

public interface TimetableSchedulingService {

	void createScheduledTimetables();

	void deleteExpiredTimetables();
}
