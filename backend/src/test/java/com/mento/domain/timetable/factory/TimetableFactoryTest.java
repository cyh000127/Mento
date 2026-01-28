package com.mento.domain.timetable.factory;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableStatus;

@DisplayName("TimetableFactory 테스트")
class TimetableFactoryTest {

	private final TimetableFactory factory = new TimetableFactory();

	@Test
	@DisplayName("단일 타임테이블 생성 성공")
	void 단일_타임테이블_생성() {
		// given
		LocalDate date = LocalDate.of(2025, 1, 27);
		LocalTime time = LocalTime.of(10, 0);

		// when
		Timetable timetable = factory.createTimetable(date, time);

		// then
		assertThat(timetable.getScheduledDate()).isEqualTo(date);
		assertThat(timetable.getScheduledTime()).isEqualTo(LocalTime.of(10, 0));
		assertThat(timetable.getStatus()).isEqualTo(TimetableStatus.ACTIVE);
		assertThat(timetable.getMaxCapacity()).isEqualTo(15);
		assertThat(timetable.getCurrentCapacity()).isZero();
	}

	@Test
	@DisplayName("일일 타임테이블 생성 개수 검증")
	void 일일_타임테이블_생성_개수_검증() {
		// given
		LocalDate date = LocalDate.of(2025, 1, 27);

		// when
		List<Timetable> timetables = factory.createDailyTimetables(date);

		// then
		assertThat(timetables).hasSize(9);
	}

	@Test
	@DisplayName("일일 타임테이블 생성 시간대 검증")
	void 일일_타임테이블_생성_시간대_검증() {
		// given
		LocalDate date = LocalDate.of(2025, 1, 27);

		// when
		List<Timetable> timetables = factory.createDailyTimetables(date);

		// then
		// 9시~17시 입력 → 18시~2시(다음날) 저장 (UTC+9 offset 적용)
		List<Integer> expectedHours = List.of(9, 10, 11, 12, 13, 14, 15, 16, 17);

		assertThat(timetables)
			.extracting(timetable -> timetable.getScheduledTime().getHour())
			.containsExactlyElementsOf(expectedHours);

		assertThat(timetables)
			.allMatch(t -> t.getScheduledTime().getMinute() == 0);
	}

	@Test
	@DisplayName("일일 타임테이블 Active 상태 검증")
	void 일일_타임테이블_Active_상태_검증() {
		// given
		LocalDate date = LocalDate.of(2025, 1, 27);

		// when
		List<Timetable> timetables = factory.createDailyTimetables(date);

		// then
		assertThat(timetables).isNotEmpty()
			.allMatch(t -> t.getStatus() == TimetableStatus.ACTIVE)
			.allMatch(t -> t.getMaxCapacity() == 15)
			.allMatch(t -> t.getCurrentCapacity() == 0)
			.allMatch(t -> t.getScheduledDate().equals(date));
	}

	@Test
	@DisplayName("일일 타임테이블 생성 날짜 올바르게 설정됨")
	void 일일_타임테이블_날짜_검증() {
		// given
		LocalDate date = LocalDate.of(2025, 1, 27);

		// when
		List<Timetable> timetables = factory.createDailyTimetables(date);

		// then
		assertThat(timetables)
			.extracting(Timetable::getScheduledDate)
			.containsOnly(date);
	}
}
