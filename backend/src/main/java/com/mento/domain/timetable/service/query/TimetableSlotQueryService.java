package com.mento.domain.timetable.service.query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.mento.domain.timetable.entity.TimetableSlot;

public interface TimetableSlotQueryService {

	TimetableSlot findById(Long slotId);

	TimetableSlot findByTimetableIdAndTypeId(Long timetableId, Long typeId);

	List<TimetableSlot> findAllByTimetableIdsAndTypeId(List<Long> timetableIds, Long typeId);

	List<TimetableSlot> findAllByTimetableIds(final List<Long> timetableIds);

	List<TimetableSlot> findAllActiveSlotsBefore(final LocalDate date, final LocalTime time);
}
