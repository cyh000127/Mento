package com.mento.domain.timetable.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.exceptioon.TimeTableException;
import com.mento.domain.timetable.repository.TimetableRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTableQueryServiceImpl implements TimeTableQueryService {
	private final TimetableRepository timetableRepository;

	@Override
	public Timetable findByReservationId(final Long timetableId) {
		Timetable timetable = timetableRepository.findByTimetableId(timetableId)
			.orElseThrow(() -> new TimeTableException(ErrorCode.TIMETABLE_NOT_FOUND));
		log.info("[TimeTable] 시간표 조회 완료 {timetableId: {}}", timetable.getId());
		return timetable;
	}
}
