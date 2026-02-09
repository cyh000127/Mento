package com.mento.domain.timetable.service.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.util.TimeUtils;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.service.command.TimetableCommandService;
import com.mento.domain.timetable.service.command.TimetableSlotCommandService;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.service.query.TimetableSlotQueryService;
import com.mento.domain.timetable.strategy.TimetableGenerationStrategy;
import com.mento.domain.timetable.vo.DateRange;
import com.mento.domain.timetable.vo.TimetableGenerationResult;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableSchedulingServiceImpl implements TimetableSchedulingService {

	private final TimetableSlotCommandService timetableSlotCommandService;
	private final TimetableSlotQueryService timetableSlotQueryService;

	private final TimetableCommandService timetableCommandService;
	private final TimetableQueryService timetableQueryService;

	private final TimetableGenerationStrategy timetableGenerationStrategy;

	@Override
	@Scheduled(cron = "0 0 0 * * *")
	public void createScheduledTimetables() {
		LocalDate now = TimeUtils.nowAsLocalDate();
		DateRange dateRange = DateRange.ofOneMonthFromToday(now);

		TimetableGenerationResult result = timetableGenerationStrategy.generateForMissingDates(dateRange);

		if (result.isEmpty()) {
			log.warn("[TimetableScheduling] 생성할 타임테이블이 없습니다 {날짜: {}}", now);
			return;
		}

		List<Timetable> savedTimetables = timetableCommandService.saveAll(result.getTimetables());
		log.info("[TimetableScheduling] 타임테이블 생성 완료 {날짜: {}, 개수: {}}", now, savedTimetables.size());

		List<TimetableSlot> slots = timetableGenerationStrategy.generateSlotsForTimetables(savedTimetables);

		if (slots.isEmpty()) {
			log.warn("[TimetableScheduling] 생성할 슬롯이 없습니다 (MentorType 미존재) {날짜: {}, 타임테이블 개수: {}}", now,
				savedTimetables.size());
			return;
		}

		List<TimetableSlot> savedSlots = timetableSlotCommandService.saveAll(slots);
		log.info("[TimetableScheduling] 타임테이블 슬롯 생성 완료 {날짜: {}, 슬롯 개수: {}, 타임테이블당 슬롯: {}}", now,
			savedSlots.size(), savedSlots.size() / savedTimetables.size());
	}

	@Override
	@Scheduled(cron = "0 0 0 * * *")
	public void deleteExpiredTimetables() {
		LocalDate now = TimeUtils.nowAsLocalDate();

		List<Timetable> expiredTimetables = timetableQueryService.findAllExpiredTimetables(now);
		expiredTimetables.forEach(Timetable::withdraw);

		List<Long> timetableIds = expiredTimetables.stream()
			.map(Timetable::getId)
			.toList();

		List<TimetableSlot> expiredSlots = timetableSlotQueryService.findAllByTimetableIds(timetableIds);
		expiredSlots.forEach(TimetableSlot::withdraw);

		log.info("[TimetableScheduling] 만료된 타임테이블 삭제 완료 {날짜: {}, 타임테이블: {}개, 슬롯: {}개}",
			now, expiredTimetables.size(), expiredSlots.size());
	}

	@Override
	@Scheduled(cron = "0 0 * * * *")
	public void expirePastTimetableSlots() {
		LocalDateTime now = TimeUtils.nowAsLocalDateTime();
		LocalDate currentDate = now.toLocalDate();
		LocalTime currentTime = now.toLocalTime();

		List<TimetableSlot> expiredSlots = timetableSlotQueryService.findAllActiveSlotsBefore(currentDate, currentTime);

		if (expiredSlots.isEmpty()) {
			return;
		}

		expiredSlots.forEach(TimetableSlot::withdraw);
		log.info("[TimetableScheduling] 지난 타임테이블 슬롯 만료 처리 완료 {기준시간: {}, 처리건수: {}건}", now, expiredSlots.size());
	}
}

