package com.mento.domain.timetable.service.command;

import java.util.List;

import com.mento.domain.timetable.entity.TimetableSlot;

public interface TimetableSlotCommandService {

	List<TimetableSlot> saveAll(List<TimetableSlot> slots);
}
