package com.mento.domain.timetable.service.schedule;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.factory.TimetableFactory;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.strategy.TimetableGenerationStrategy;
import com.mento.domain.timetable.vo.DateRange;
import com.mento.domain.timetable.vo.TimetableGenerationResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimetableGenerationStrategy 테스트")
class TimetableGenerationStrategyTest {

	@Mock
	private TimetableQueryService queryService;

	@Mock
	private TimetableFactory timetableFactory;

	@InjectMocks
	private TimetableGenerationStrategy generationStrategy;

	@Test
	@DisplayName("기존에 없는 날짜에 대해서만 타임테이블을 생성한다")
	void 기존에_없는_날짜에_대해서만_타임테이블을_생성한다() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 3);
		DateRange dateRange = DateRange.of(startDate, endDate);

		Set<LocalDate> existingDates = Set.of(LocalDate.of(2025, 1, 2));

		List<Timetable> mockTimetables = createMockTimetables(LocalDate.of(2025, 1, 1));

		when(queryService.findExistingDatesInRange(startDate, endDate))
			.thenReturn(existingDates);
		when(timetableFactory.createDailyTimetables(any(LocalDate.class)))
			.thenReturn(mockTimetables);

		// when
		TimetableGenerationResult result = generationStrategy.generateForMissingDates(dateRange);

		// then
		assertThat(result.getTimetables()).isNotEmpty();
		verify(queryService, times(1)).findExistingDatesInRange(startDate, endDate);
		verify(timetableFactory, times(2)).createDailyTimetables(any(LocalDate.class));
	}

	@Test
	@DisplayName("범위 내 모든 날짜가 이미 존재하면 빈 리스트를 반환한다")
	void 범위_내_모든_날짜가_이미_존재하면_빈_리스트를_반환한다() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 3);
		DateRange dateRange = DateRange.of(startDate, endDate);

		Set<LocalDate> existingDates = Set.of(
			LocalDate.of(2025, 1, 1),
			LocalDate.of(2025, 1, 2),
			LocalDate.of(2025, 1, 3)
		);

		when(queryService.findExistingDatesInRange(startDate, endDate))
			.thenReturn(existingDates);

		// when
		TimetableGenerationResult result = generationStrategy.generateForMissingDates(dateRange);

		// then
		assertThat(result.isEmpty()).isTrue();
		verify(timetableFactory, never()).createDailyTimetables(any(LocalDate.class));
	}

	@Test
	@DisplayName("기존 타임테이블이 없으면 범위 내 모든 날짜의 타임테이블을 생성한다")
	void 기존_타임테이블이_없으면_범위_내_모든_날짜의_타임테이블을_생성한다() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 2);
		DateRange dateRange = DateRange.of(startDate, endDate);

		Set<LocalDate> existingDates = Set.of();

		List<Timetable> mockTimetables = createMockTimetables(LocalDate.of(2025, 1, 1));

		when(queryService.findExistingDatesInRange(startDate, endDate))
			.thenReturn(existingDates);
		when(timetableFactory.createDailyTimetables(any(LocalDate.class)))
			.thenReturn(mockTimetables);

		// when
		TimetableGenerationResult result = generationStrategy.generateForMissingDates(dateRange);

		// then
		assertThat(result.getTimetables()).isNotEmpty();
		verify(timetableFactory, times(2)).createDailyTimetables(any(LocalDate.class));
	}

	private List<Timetable> createMockTimetables(final LocalDate date) {
		return List.of(
			createMockTimetable(date, 9),
			createMockTimetable(date, 10),
			createMockTimetable(date, 11)
		);
	}

	private Timetable createMockTimetable(final LocalDate date, final int hour) {
		return Timetable.builder()
			.scheduledDate(date)
			.scheduledTime(LocalTime.of(hour, 0))
			.build();
	}
}
