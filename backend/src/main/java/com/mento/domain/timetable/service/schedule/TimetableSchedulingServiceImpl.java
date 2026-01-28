package com.mento.domain.timetable.service.schedule;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.mento.common.util.TimeUtils;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.service.command.TimetableCommandService;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.vo.DateRange;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableSchedulingServiceImpl implements TimetableSchedulingService {

	private final TimetableCommandService commandService;
	private final TimetableQueryService timetableQueryService;

	private final TimetableGenerationStrategy timetableGenerationStrategy;

	@Override
	@Scheduled(cron = "0 0 0 * * *")
	public void createScheduledTimetables() {
		LocalDate now = TimeUtils.nowAsLocalDate();
		DateRange dateRange = DateRange.ofOneMonthFromToday(now);
		List<Timetable> timetables = timetableGenerationStrategy.generateForMissingDates(dateRange);

		if (!CollectionUtils.isEmpty(timetables)) {
			List<Timetable> timetableList = commandService.saveAll(timetables);
			log.info("[TimetableScheduling] 타임테이블 스케줄링 생성 완료 {날짜: {}, 개수: {}}", now, timetableList.size());
		} else {
			log.warn("[TimetableScheduling] 생성할 타임테이블이 없습니다 {날짜: {}}", now);
		}
	}

	@Override
	@Scheduled(cron = "0 0 0 * * *")
	public void deleteExpiredTimetables() {
		LocalDate now = TimeUtils.nowAsLocalDate();
		timetableQueryService.findAllExpiredTimetables(now).forEach(Timetable::withdraw);
		log.info("[TimetableScheduling] 만료된 타임테이블 삭제 완료 {날짜: {}}", now);
	}
}
