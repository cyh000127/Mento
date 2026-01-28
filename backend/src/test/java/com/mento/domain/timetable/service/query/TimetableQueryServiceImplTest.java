package com.mento.domain.timetable.service.query;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableStatus;
import com.mento.domain.timetable.repository.TimetableRepository;
import com.mento.domain.timetable.vo.DateRange;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimetableQueryServiceImpl 테스트")
class TimetableQueryServiceImplTest {

	@Mock
	private TimetableRepository timetableRepository;

	@InjectMocks
	private TimetableQueryServiceImpl timetableQueryService;

	@Test
	@DisplayName("날짜 범위로 타임테이블 조회 성공")
	void 날짜_범위로_타임테이블_조회_성공() {
		// given
		LocalDate startDate = LocalDate.of(2026, 1, 28);
		LocalDate endDate = LocalDate.of(2026, 2, 27);
		DateRange dateRange = DateRange.of(startDate, endDate);

		List<Timetable> mockTimetables = createMockTimetablesForRange(startDate);

		when(timetableRepository.findAllByScheduledDateBetween(startDate, endDate))
			.thenReturn(mockTimetables);

		// when
		List<Timetable> result = timetableQueryService.findAllByDateRange(dateRange);

		// then
		assertThat(result).hasSize(9).allMatch(t ->
			!t.getScheduledDate().isBefore(startDate) && !t.getScheduledDate().isAfter(endDate)
		);
		verify(timetableRepository, times(1)).findAllByScheduledDateBetween(startDate, endDate);
	}

	@Test
	@DisplayName("날짜 범위로 타임테이블 조회 시 빈 결과 반환")
	void 날짜_범위로_타임테이블_조회_시_빈_결과_반환() {
		// given
		LocalDate startDate = LocalDate.of(2026, 3, 1);
		LocalDate endDate = LocalDate.of(2026, 3, 31);
		DateRange dateRange = DateRange.of(startDate, endDate);

		when(timetableRepository.findAllByScheduledDateBetween(startDate, endDate))
			.thenReturn(List.of());

		// when
		List<Timetable> result = timetableQueryService.findAllByDateRange(dateRange);

		// then
		assertThat(result).isEmpty();
		verify(timetableRepository, times(1)).findAllByScheduledDateBetween(startDate, endDate);
	}

	@Test
	@DisplayName("날짜별로 정렬된 타임테이블 조회")
	void 날짜별로_정렬된_타임테이블_조회() {
		// given
		LocalDate startDate = LocalDate.of(2026, 1, 28);
		LocalDate endDate = LocalDate.of(2026, 1, 30);
		DateRange dateRange = DateRange.of(startDate, endDate);

		List<Timetable> mockTimetables = List.of(
			createMockTimetable(1L, LocalDate.of(2026, 1, 28), LocalTime.of(9, 0)),
			createMockTimetable(2L, LocalDate.of(2026, 1, 28), LocalTime.of(10, 0)),
			createMockTimetable(3L, LocalDate.of(2026, 1, 29), LocalTime.of(9, 0)),
			createMockTimetable(4L, LocalDate.of(2026, 1, 30), LocalTime.of(9, 0))
		);

		when(timetableRepository.findAllByScheduledDateBetween(startDate, endDate))
			.thenReturn(mockTimetables);

		// when
		List<Timetable> result = timetableQueryService.findAllByDateRange(dateRange);

		// then
		assertThat(result).hasSize(4);
		// 날짜 순서 확인
		assertThat(result.get(0).getScheduledDate()).isEqualTo(LocalDate.of(2026, 1, 28));
		assertThat(result.get(2).getScheduledDate()).isEqualTo(LocalDate.of(2026, 1, 29));
		assertThat(result.get(3).getScheduledDate()).isEqualTo(LocalDate.of(2026, 1, 30));
		verify(timetableRepository, times(1)).findAllByScheduledDateBetween(startDate, endDate);
	}

	private Timetable createMockTimetable(
		final Long id,
		final LocalDate date,
		final LocalTime time
	) {
		return Timetable.builder()
			.id(id)
			.scheduledDate(date)
			.scheduledTime(time)
			.status(TimetableStatus.ACTIVE)
			.maxCapacity(15)
			.currentCapacity(0)
			.build();
	}

	private List<Timetable> createMockTimetablesForRange(final LocalDate baseDate) {
		return List.of(
			createMockTimetable(1L, baseDate, LocalTime.of(9, 0)),
			createMockTimetable(2L, baseDate, LocalTime.of(10, 0)),
			createMockTimetable(3L, baseDate, LocalTime.of(11, 0)),
			createMockTimetable(4L, baseDate, LocalTime.of(12, 0)),
			createMockTimetable(5L, baseDate, LocalTime.of(13, 0)),
			createMockTimetable(6L, baseDate, LocalTime.of(14, 0)),
			createMockTimetable(7L, baseDate, LocalTime.of(15, 0)),
			createMockTimetable(8L, baseDate, LocalTime.of(16, 0)),
			createMockTimetable(9L, baseDate, LocalTime.of(17, 0))
		);
	}
}
