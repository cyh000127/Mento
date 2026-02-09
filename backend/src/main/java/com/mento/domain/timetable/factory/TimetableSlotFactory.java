package com.mento.domain.timetable.factory;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.timetable.entity.SlotStatus;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableSlotFactory {

	private static final int SLOT_MAX_CAPACITY = 5;

	public List<TimetableSlot> createSlotsForTimetable(final Timetable timetable, List<MentorType> mentorTypes) {
		if (CollectionUtils.isEmpty(mentorTypes)) {
			log.warn("[TimetableSlot] MentorType이 존재하지 않습니다. Slot 생성 불가 {timetableId: {}}", timetable.getId());
			return List.of();
		}

		return mentorTypes.stream()
			.map(mentorType -> createSlot(timetable, mentorType))
			.toList();
	}

	private TimetableSlot createSlot(final Timetable timetable, final MentorType mentorType) {
		TimetableSlot timetableSlot = TimetableSlot.builder()
			.maxCapacity(SLOT_MAX_CAPACITY)
			.currentCapacity(0)
			.status(SlotStatus.AVAILABLE)
			.build();

		timetableSlot.assignTimetable(timetable);
		timetableSlot.assignMentorType(mentorType);

		return timetableSlot;
	}
}
