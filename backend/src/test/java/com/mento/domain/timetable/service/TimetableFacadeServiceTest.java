package com.mento.domain.timetable.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

import com.mento.domain.timetable.dto.response.MonthlyTimetableResDto;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableStatus;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.vo.DateRange;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimetableFacadeService 테스트")
class TimetableFacadeServiceTest {

	@Mock
	private TimetableQueryService timetableQueryService;

	@InjectMocks
	private TimetableFacadeService timetableFacadeService;

	@Test
	@DisplayName("월별 타임테이블 조회 성공")
	void 월별_타임테이블_조회_성공() {
		// given
		LocalDate baseDate = LocalDate.of(2026, 1, 28);
		List<Timetable> mockTimetables = createMockTimetables(baseDate);

		when(timetableQueryService.findAllByDateRange(any(DateRange.class)))
			.thenReturn(mockTimetables);

		// when
		MonthlyTimetableResDto result = timetableFacadeService.getMonthlyTimetables(baseDate);

		// then
		assertThat(result).isNotNull();
		assertThat(result.startDate()).isEqualTo(baseDate);
		assertThat(result.endDate()).isEqualTo(baseDate.plusDays(30));
		assertThat(result.totalDays()).isEqualTo(31);
		assertThat(result.dailyTimetables()).hasSize(31);
		verify(timetableQueryService, times(1)).findAllByDateRange(any(DateRange.class));
	}

	@Test
	@DisplayName("null 날짜로 조회 시 오늘 날짜 기준으로 조회")
	void null_날짜로_조회_시_오늘_날짜_기준으로_조회() {
		// given
		when(timetableQueryService.findAllByDateRange(any(DateRange.class)))
			.thenReturn(List.of());

		// when
		MonthlyTimetableResDto result = timetableFacadeService.getMonthlyTimetables(null);

		// then
		assertThat(result).isNotNull();
		assertThat(result.startDate()).isEqualTo(LocalDate.now());
		assertThat(result.totalDays()).isEqualTo(31);
		verify(timetableQueryService, times(1)).findAllByDateRange(any(DateRange.class));
	}

	@Test
	@DisplayName("타임테이블이 없는 경우 빈 리스트 반환")
	void 타임테이블이_없는_경우_빈_리스트_반환() {
		// given
		LocalDate baseDate = LocalDate.of(2026, 3, 1);

		when(timetableQueryService.findAllByDateRange(any(DateRange.class)))
			.thenReturn(List.of());

		// when
		MonthlyTimetableResDto result = timetableFacadeService.getMonthlyTimetables(baseDate);

		// then
		assertThat(result).isNotNull();
		assertThat(result.dailyTimetables()).hasSize(31);
		assertThat(result.dailyTimetables())
			.allMatch(daily -> daily.timetables().isEmpty());
		verify(timetableQueryService, times(1)).findAllByDateRange(any(DateRange.class));
	}

	@Test
	@DisplayName("특정 날짜의 타임테이블만 포함된 경우 정상 조회")
	void 특정_날짜의_타임테이블만_포함된_경우_정상_조회() {
		// given
		LocalDate baseDate = LocalDate.of(2026, 1, 28);
		LocalDate targetDate = baseDate.plusDays(5);
		List<Timetable> mockTimetables = createMockTimetables(targetDate);

		when(timetableQueryService.findAllByDateRange(any(DateRange.class)))
			.thenReturn(mockTimetables);

		// when
		MonthlyTimetableResDto result = timetableFacadeService.getMonthlyTimetables(baseDate);

		// then
		assertThat(result).isNotNull();
		assertThat(result.dailyTimetables()).hasSize(31);

		long daysWithTimetables = result.dailyTimetables().stream()
			.filter(daily -> !daily.timetables().isEmpty())
			.count();

		assertThat(daysWithTimetables).isEqualTo(1);
		verify(timetableQueryService, times(1)).findAllByDateRange(any(DateRange.class));
	}

	private List<Timetable> createMockTimetables(final LocalDate date) {
		return List.of(
			createMockTimetable(1L, date, LocalTime.of(9, 0)),
			createMockTimetable(2L, date, LocalTime.of(10, 0)),
			createMockTimetable(3L, date, LocalTime.of(11, 0)),
			createMockTimetable(4L, date, LocalTime.of(12, 0)),
			createMockTimetable(5L, date, LocalTime.of(13, 0)),
			createMockTimetable(6L, date, LocalTime.of(14, 0)),
			createMockTimetable(7L, date, LocalTime.of(15, 0)),
			createMockTimetable(8L, date, LocalTime.of(16, 0)),
			createMockTimetable(9L, date, LocalTime.of(17, 0))
		);
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
}
