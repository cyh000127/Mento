package com.mento.domain.timetable.factory;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.timetable.entity.SlotStatus;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;

@DisplayName("TimetableSlotFactory 테스트")
class TimetableSlotFactoryTest {

	private final TimetableSlotFactory timetableSlotFactory = new TimetableSlotFactory();

	@Test
	@DisplayName("타임테이블에 대해 멘토 유형별로 슬롯을 생성한다")
	void 타임테이블에_대해_멘토_유형별로_슬롯을_생성한다() {
		// given
		Timetable timetable = createMockTimetable(LocalDate.now(), 9);
		List<MentorType> mentorTypes = List.of(
			createMockMentorType(1L, "스킨케어"),
			createMockMentorType(2L, "뷰티"),
			createMockMentorType(3L, "헤어")
		);

		// when
		List<TimetableSlot> slots = timetableSlotFactory.createSlotsForTimetable(timetable, mentorTypes);

		// then
		assertThat(slots).hasSize(3)
			.allMatch(slot -> slot.getMaxCapacity() == 5)
			.allMatch(slot -> slot.getCurrentCapacity() == 0)
			.allMatch(slot -> slot.getStatus() == SlotStatus.AVAILABLE);
	}

	@Test
	@DisplayName("멘토 유형이 없으면 빈 슬롯 목록을 반환한다")
	void 멘토_유형이_없으면_빈_슬롯_목록을_반환한다() {
		// given
		Timetable timetable = createMockTimetable(LocalDate.now(), 9);

		// when
		List<TimetableSlot> slots = timetableSlotFactory.createSlotsForTimetable(timetable, List.of());

		// then
		assertThat(slots).isEmpty();
	}

	@Test
	@DisplayName("슬롯의 maxCapacity는 5로 고정된다")
	void 슬롯의_maxCapacity는_5로_고정된다() {
		// given
		Timetable timetable = createMockTimetable(LocalDate.now(), 9);
		List<MentorType> mentorTypes = List.of(createMockMentorType(1L, "스킨케어"));

		// when
		List<TimetableSlot> slots = timetableSlotFactory.createSlotsForTimetable(timetable, mentorTypes);

		// then
		assertThat(slots.getFirst().getMaxCapacity()).isEqualTo(5);
	}

	private Timetable createMockTimetable(final LocalDate date, final int hour) {
		return Timetable.builder()
			.scheduledDate(date)
			.scheduledTime(LocalTime.of(hour, 0))
			.build();
	}

	private MentorType createMockMentorType(final Long id, final String typeName) {
		final MentorType mentorType = MentorType.builder()
			.typeName(typeName)
			.price(50000)
			.description("테스트용 멘토 유형")
			.build();
		if (id != null) {
			ReflectionTestUtils.setField(mentorType, "id", id);
		}
		return mentorType;
	}
}
