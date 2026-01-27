package com.mento.domain.timetable.service.command;

import java.util.List;

import com.mento.domain.timetable.entity.Timetable;

public interface TimetableCommandService {

	List<Timetable> saveAll(List<Timetable> timetables);
}