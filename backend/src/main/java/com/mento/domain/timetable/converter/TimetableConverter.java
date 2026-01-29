package com.mento.domain.timetable.converter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.timetable.dto.response.DailyTimetableResDto;
import com.mento.domain.timetable.dto.response.MonthlyTimetableResDto;
import com.mento.domain.timetable.dto.response.common.TimetableSlotInfoDto;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.vo.DateRange;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimetableConverter {

	public TimetableSlotInfoDto toTimetableSlotInfoDto(final TimetableSlot slot) {
		Timetable timetable = slot.getTimetable();
		MentorType mentorType = slot.getMentorType();

		return TimetableSlotInfoDto.builder()
			.timetableId(timetable.getId())
			.slotId(slot.getId())
			.scheduledTime(timetable.getScheduledTime())
			.price(mentorType.getPrice())
			.maxCapacity(slot.getMaxCapacity())
			.currentCapacity(slot.getCurrentCapacity())
			.availableCapacity(slot.getAvailableCapacity())
			.status(slot.getStatus())
			.build();
	}

	public DailyTimetableResDto toDailyTimetableResDto(final LocalDate date, final List<TimetableSlot> slots) {
		List<TimetableSlotInfoDto> slotInfos = slots.stream()
			.map(TimetableConverter::toTimetableSlotInfoDto)
			.toList();

		return DailyTimetableResDto.builder()
			.date(date)
			.slots(slotInfos)
			.build();
	}

	public MonthlyTimetableResDto toMonthlyTimetableResDto(
		final DateRange dateRange,
		final MentorType mentorType,
		final List<TimetableSlot> slots
	) {
		Map<LocalDate, List<TimetableSlot>> slotsByDate = groupSlotsByDate(slots);

		List<DailyTimetableResDto> dailyTimetables = dateRange.getAllDates().stream()
			.map(date -> toDailyTimetableResDto(date, slotsByDate.getOrDefault(date, List.of())))
			.toList();

		return MonthlyTimetableResDto.builder()
			.startDate(dateRange.getStartDate())
			.endDate(dateRange.getEndDate())
			.totalDays((int)dateRange.getDayCount())
			.mentorTypeId(mentorType.getId())
			.mentorTypeName(mentorType.getTypeName())
			.dailyTimetables(dailyTimetables)
			.build();
	}

	private Map<LocalDate, List<TimetableSlot>> groupSlotsByDate(final List<TimetableSlot> slots) {
		return slots.stream()
			.collect(Collectors.groupingBy(slot -> slot.getTimetable().getScheduledDate()));
	}
}
