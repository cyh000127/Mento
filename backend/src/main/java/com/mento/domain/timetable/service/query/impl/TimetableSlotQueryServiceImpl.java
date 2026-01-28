package com.mento.domain.timetable.service.query.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.repository.TimetableSlotRepository;
import com.mento.domain.timetable.service.query.TimetableSlotQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableSlotQueryServiceImpl implements TimetableSlotQueryService {

	private final TimetableSlotRepository timetableSlotRepository;

	@Override
	public List<TimetableSlot> findAllByTimetableIdsAndTypeId(final List<Long> timetableIds, final Long typeId) {
		return timetableSlotRepository.findAllByTimetableIdsAndTypeId(timetableIds, typeId);
	}

	@Override
	public List<TimetableSlot> findAllByTimetableIds(final List<Long> timetableIds) {
		return timetableSlotRepository.findAllByTimetableIds(timetableIds);
	}
}
