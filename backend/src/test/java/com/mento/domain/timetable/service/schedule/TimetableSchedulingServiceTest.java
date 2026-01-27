package com.mento.domain.timetable.service.schedule;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableStatus;
import com.mento.domain.timetable.service.command.TimetableCommandService;
import com.mento.domain.timetable.service.query.TimetableQueryService;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimetableSchedulingService 테스트")
class TimetableSchedulingServiceTest {

	@Mock
	private TimetableCommandService commandService;

	@Mock
	private TimetableQueryService queryService;

	@Mock
	private TimetableGenerationStrategy generationStrategy;

	@InjectMocks
	private TimetableSchedulingServiceImpl schedulingService;

	@Test
	@DisplayName("스케줄링 실행 시 타임테이블을 생성하고 저장한다")
	void 스케줄링_실행_시_타임테이블을_생성하고_저장한다() {
		// given
		LocalDate today = LocalDate.now();
		List<Timetable> mockTimetables = createMockTimetables(today);

		when(generationStrategy.generateForMissingDates(any()))
			.thenReturn(mockTimetables);
		when(commandService.saveAll(anyList()))
			.thenReturn(mockTimetables);

		// when
		schedulingService.createScheduledTimetables();

		// then
		verify(generationStrategy, times(1)).generateForMissingDates(any());
		verify(commandService, times(1)).saveAll(mockTimetables);
	}

	@Test
	@DisplayName("생성된 타임테이블 목록을 모두 저장한다")
	void 생성된_타임테이블_목록을_모두_저장한다() {
		// given
		LocalDate today = LocalDate.now();
		List<Timetable> mockTimetables = createMockTimetables(today);

		when(generationStrategy.generateForMissingDates(any()))
			.thenReturn(mockTimetables);
		when(commandService.saveAll(anyList()))
			.thenReturn(mockTimetables);

		// when
		schedulingService.createScheduledTimetables();

		// then
		ArgumentCaptor<List<Timetable>> captor = ArgumentCaptor.forClass(List.class);
		verify(commandService).saveAll(captor.capture());

		List<Timetable> savedTimetables = captor.getValue();
		assertThat(savedTimetables).isEqualTo(mockTimetables).hasSize(9);
	}

	@Test
	@DisplayName("생성할 타임테이블이 없으면 저장하지 않는다")
	void 생성할_타임테이블이_없으면_저장하지_않는다() {
		// given
		when(generationStrategy.generateForMissingDates(any()))
			.thenReturn(List.of());

		// when
		schedulingService.createScheduledTimetables();

		// then
		verify(commandService, never()).saveAll(anyList());
	}

	@Test
	@DisplayName("만료된 타임테이블을 조회하여 삭제한다")
	void 만료된_타임테이블을_조회하여_삭제한다() {
		// given
		List<Timetable> expiredTimetables = createMockTimetables(LocalDate.now().minusDays(1));
		when(queryService.findAllExpiredTimetables(any(LocalDate.class)))
			.thenReturn(expiredTimetables);

		// when
		schedulingService.deleteExpiredTimetables();

		// then
		verify(queryService, times(1)).findAllExpiredTimetables(any(LocalDate.class));
	}

	private List<Timetable> createMockTimetables(final LocalDate date) {
		return List.of(
			createMockTimetable(date, 9),
			createMockTimetable(date, 10),
			createMockTimetable(date, 11),
			createMockTimetable(date, 12),
			createMockTimetable(date, 13),
			createMockTimetable(date, 14),
			createMockTimetable(date, 15),
			createMockTimetable(date, 16),
			createMockTimetable(date, 17)
		);
	}

	private Timetable createMockTimetable(final LocalDate date, final int hour) {
		return Timetable.builder()
			.scheduledDate(date)
			.scheduledTime(LocalTime.of(hour, 0))
			.status(TimetableStatus.ACTIVE)
			.maxCapacity(15)
			.currentCapacity(0)
			.build();
	}
}
