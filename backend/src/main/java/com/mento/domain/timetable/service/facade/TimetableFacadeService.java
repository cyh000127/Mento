package com.mento.domain.timetable.service.facade;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.util.TimeUtils;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.mentor.service.query.MentorTypeQueryService;
import com.mento.domain.timetable.converter.TimetableConverter;
import com.mento.domain.timetable.dto.response.MonthlyTimetableResDto;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.service.query.TimetableSlotQueryService;
import com.mento.domain.timetable.vo.DateRange;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableFacadeService {

	private final TimetableQueryService timetableQueryService;
	private final TimetableSlotQueryService timetableSlotQueryService;
	private final MentorTypeQueryService mentorTypeQueryService;

	public MonthlyTimetableResDto getMonthlyTimetables(final Long typeId) {
		DateRange dateRange = DateRange.ofOneMonthFromToday(TimeUtils.nowAsLocalDate());

		MentorType mentorType = mentorTypeQueryService.findById(typeId);
		List<Timetable> timetables = timetableQueryService.findAllByDateRange(dateRange);
		List<TimetableSlot> slots = findSlotsByTimetablesAndType(timetables, typeId);

		return TimetableConverter.toMonthlyTimetableResDto(dateRange, mentorType, slots);
	}

	private List<TimetableSlot> findSlotsByTimetablesAndType(final List<Timetable> timetables, final Long typeId) {
		List<Long> timetableIds = extractTimetableIds(timetables);
		return timetableSlotQueryService.findAllByTimetableIdsAndTypeId(timetableIds, typeId);
	}

	private List<Long> extractTimetableIds(final List<Timetable> timetables) {
		return timetables.stream()
			.map(Timetable::getId)
			.toList();
	}
}