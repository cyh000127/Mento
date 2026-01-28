package com.mento.domain.timetable.service.query;

import java.util.List;

import com.mento.domain.timetable.entity.TimetableSlot;

public interface TimetableSlotQueryService {

	List<TimetableSlot> findAllByTimetableIdsAndTypeId(List<Long> timetableIds, Long typeId);

	List<TimetableSlot> findAllByTimetableIds(final List<Long> timetableIds);
}
