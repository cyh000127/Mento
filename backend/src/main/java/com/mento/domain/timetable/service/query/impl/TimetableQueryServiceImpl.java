package com.mento.domain.timetable.service.query.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.exception.TimetableException;
import com.mento.domain.timetable.repository.TimetableRepository;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.vo.DateRange;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableQueryServiceImpl implements TimetableQueryService {
	private final TimetableRepository timetableRepository;

	@Override
	public Timetable findById(final Long id) {
		Timetable timetable = timetableRepository.findById(id)
			.orElseThrow(() -> new TimetableException(ErrorCode.TIMETABLE_NOT_FOUND));
		log.info("[Timetable] 시간표 조회 완료 {id: {}}", timetable.getId());
		return timetable;
	}

	@Override
	public Timetable findByReservationId(final Long timetableId) {
		Timetable timetable = timetableRepository.findByTimetableId(timetableId)
			.orElseThrow(() -> new TimetableException(ErrorCode.TIMETABLE_NOT_FOUND));
		log.info("[Timetable] 시간표 조회 완료 {id: {}}", timetable.getId());
		return timetable;
	}

	@Override
	public List<Timetable> findAllExpiredTimetables(final LocalDate now) {
		List<Timetable> timetables = timetableRepository.findAllByScheduledDateBefore(now);
		log.info("[Timetable] 만료된 시간표 조회 완료 size : {}", timetables.size());
		return timetables;
	}

	@Override
	public Set<LocalDate> findExistingDatesInRange(final LocalDate startDate, final LocalDate endDate) {
		List<LocalDate> localDates = timetableRepository.findDistinctDatesBetween(startDate, endDate);
		log.info("[Timetable] 시간표가 생성되지 않은 일자 조회 완료 size : {}", localDates);
		return new HashSet<>(localDates);
	}

	@Override
	public List<Timetable> findAllByDateRange(final DateRange dateRange) {
		List<Timetable> timetables = timetableRepository.findAllByScheduledDateBetween(
			dateRange.getStartDate(),
			dateRange.getEndDate()
		);
		log.info("[Timetable] 기간별 시간표 조회 완료 {startDate: {}, endDate: {}, size: {}}",
			dateRange.getStartDate(),
			dateRange.getEndDate(),
			timetables.size()
		);
		return timetables;
	}

	@Override
	public List<Timetable> findAllByDateAndTime(final LocalDate date, final LocalTime time) {
		List<Timetable> timetables = timetableRepository.findByScheduledDateAndScheduledTime(date, time);
		log.info("[Timetable] 일시별 시간표 조회 완료 {date: {}, time: {}, size: {}}", date, time, timetables.size());
		return timetables;
	}
}
