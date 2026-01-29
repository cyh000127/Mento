package com.mento.domain.timetable.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.mentor.repository.MentorTypeRepository;
import com.mento.domain.timetable.dto.response.MonthlyTimetableResDto;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.service.query.TimetableSlotQueryService;
import com.mento.domain.timetable.vo.DateRange;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimetableFacadeService 테스트")
class TimetableFacadeServiceTest {

	@Mock
	private TimetableQueryService timetableQueryService;

	@Mock
	private TimetableSlotQueryService timetableSlotQueryService;

	@Mock
	private MentorTypeRepository mentorTypeRepository;

	@InjectMocks
	private TimetableFacadeService timetableFacadeService;

	@Test
	@DisplayName("월별 타임테이블 조회 성공")
	void 월별_타임테이블_조회_성공() {
		// given
		Long typeId = 1L;
		LocalDate today = LocalDate.now();
		MentorType mockMentorType = createMockMentorType(typeId, "스킨케어");
		List<Timetable> mockTimetables = createMockTimetables(today);
		List<TimetableSlot> mockSlots = createMockSlots(mockTimetables, mockMentorType);

		when(mentorTypeRepository.findById(typeId))
			.thenReturn(Optional.of(mockMentorType));
		when(timetableQueryService.findAllByDateRange(any(DateRange.class)))
			.thenReturn(mockTimetables);
		when(timetableSlotQueryService.findAllByTimetableIdsAndTypeId(anyList(), eq(typeId)))
			.thenReturn(mockSlots);

		// when
		MonthlyTimetableResDto result = timetableFacadeService.getMonthlyTimetables(typeId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.mentorTypeId()).isEqualTo(typeId);
		assertThat(result.mentorTypeName()).isEqualTo("스킨케어");
		assertThat(result.startDate()).isEqualTo(today);
		assertThat(result.endDate()).isEqualTo(today.plusMonths(1));
		verify(mentorTypeRepository, times(1)).findById(typeId);
		verify(timetableQueryService, times(1)).findAllByDateRange(any(DateRange.class));
		verify(timetableSlotQueryService, times(1)).findAllByTimetableIdsAndTypeId(anyList(), eq(typeId));
	}

	@Test
	@DisplayName("타임테이블이 없는 경우 빈 슬롯 반환")
	void 타임테이블이_없는_경우_빈_슬롯_반환() {
		// given
		Long typeId = 1L;
		MentorType mockMentorType = createMockMentorType(typeId, "스킨케어");

		when(mentorTypeRepository.findById(typeId))
			.thenReturn(Optional.of(mockMentorType));
		when(timetableQueryService.findAllByDateRange(any(DateRange.class)))
			.thenReturn(List.of());
		when(timetableSlotQueryService.findAllByTimetableIdsAndTypeId(anyList(), eq(typeId)))
			.thenReturn(List.of());

		// when
		MonthlyTimetableResDto result = timetableFacadeService.getMonthlyTimetables(typeId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.dailyTimetables())
			.allMatch(daily -> daily.slots().isEmpty());
		verify(mentorTypeRepository, times(1)).findById(typeId);
		verify(timetableQueryService, times(1)).findAllByDateRange(any(DateRange.class));
	}

	@Test
	@DisplayName("특정 날짜의 슬롯만 포함된 경우 정상 조회")
	void 특정_날짜의_슬롯만_포함된_경우_정상_조회() {
		// given
		Long typeId = 1L;
		LocalDate today = LocalDate.now();
		LocalDate targetDate = today.plusDays(5);
		MentorType mockMentorType = createMockMentorType(typeId, "스킨케어");
		List<Timetable> mockTimetables = createMockTimetables(targetDate);
		List<TimetableSlot> mockSlots = createMockSlots(mockTimetables, mockMentorType);

		when(mentorTypeRepository.findById(typeId))
			.thenReturn(Optional.of(mockMentorType));
		when(timetableQueryService.findAllByDateRange(any(DateRange.class)))
			.thenReturn(mockTimetables);
		when(timetableSlotQueryService.findAllByTimetableIdsAndTypeId(anyList(), eq(typeId)))
			.thenReturn(mockSlots);

		// when
		MonthlyTimetableResDto result = timetableFacadeService.getMonthlyTimetables(typeId);

		// then
		assertThat(result).isNotNull();

		long daysWithSlots = result.dailyTimetables().stream()
			.filter(daily -> !daily.slots().isEmpty())
			.count();

		assertThat(daysWithSlots).isEqualTo(1);
		verify(mentorTypeRepository, times(1)).findById(typeId);
		verify(timetableQueryService, times(1)).findAllByDateRange(any(DateRange.class));
	}

	private List<Timetable> createMockTimetables(final LocalDate date) {
		return List.of(
			createMockTimetable(1L, date, LocalTime.of(9, 0)),
			createMockTimetable(2L, date, LocalTime.of(10, 0)),
			createMockTimetable(3L, date, LocalTime.of(11, 0))
		);
	}

	private Timetable createMockTimetable(final Long id, final LocalDate date, final LocalTime time) {
		return Timetable.builder()
			.id(id)
			.scheduledDate(date)
			.scheduledTime(time)
			.build();
	}

	private MentorType createMockMentorType(final Long id, final String typeName) {
		return MentorType.builder()
			.id(id)
			.typeName(typeName)
			.price(35000)
			.description("테스트용 멘토 유형")
			.build();
	}

	private List<TimetableSlot> createMockSlots(final List<Timetable> timetables, final MentorType mentorType) {
		return timetables.stream()
			.map(timetable -> TimetableSlot.builder()
				.id(timetable.getId())
				.timetable(timetable)
				.mentorType(mentorType)
				.maxCapacity(5)
				.currentCapacity(0)
				.build())
			.toList();
	}
}
