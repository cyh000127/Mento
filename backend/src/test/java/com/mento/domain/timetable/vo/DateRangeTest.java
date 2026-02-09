package com.mento.domain.timetable.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DateRange 테스트")
class DateRangeTest {

	@Test
	@DisplayName("시작일과 종료일을 가진 DateRange를 생성한다")
	void 시작일과_종료일을_가진_DateRange를_생성한다() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 31);

		// when
		DateRange dateRange = DateRange.of(startDate, endDate);

		// then
		assertThat(dateRange.getStartDate()).isEqualTo(startDate);
		assertThat(dateRange.getEndDate()).isEqualTo(endDate);
	}

	@Test
	@DisplayName("오늘부터 한 달 후까지의 범위를 생성한다")
	void 오늘부터_한달_후까지의_범위를_생성한다() {
		// given
		LocalDate today = LocalDate.of(2025, 1, 15);

		// when
		DateRange dateRange = DateRange.ofOneMonthFromToday(today);

		// then
		assertThat(dateRange.getStartDate()).isEqualTo(today);
		assertThat(dateRange.getEndDate()).isEqualTo(LocalDate.of(2025, 2, 15));
	}

	@Test
	@DisplayName("범위 내 모든 날짜를 순서대로 반환한다")
	void 범위_내_모든_날짜를_순서대로_반환한다() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 5);
		DateRange dateRange = DateRange.of(startDate, endDate);

		// when
		List<LocalDate> allDates = dateRange.getAllDates();

		// then
		assertThat(allDates).hasSize(5).containsExactly(
			LocalDate.of(2025, 1, 1),
			LocalDate.of(2025, 1, 2),
			LocalDate.of(2025, 1, 3),
			LocalDate.of(2025, 1, 4),
			LocalDate.of(2025, 1, 5)
		);
	}

	@Test
	@DisplayName("특정 날짜가 범위에 포함되는지 확인한다")
	void 특정_날짜가_범위에_포함되는지_확인한다() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 31);
		DateRange dateRange = DateRange.of(startDate, endDate);

		// when & then
		assertThat(dateRange.contains(LocalDate.of(2025, 1, 15))).isTrue();
		assertThat(dateRange.contains(LocalDate.of(2025, 1, 1))).isTrue();
		assertThat(dateRange.contains(LocalDate.of(2025, 1, 31))).isTrue();
		assertThat(dateRange.contains(LocalDate.of(2024, 12, 31))).isFalse();
		assertThat(dateRange.contains(LocalDate.of(2025, 2, 1))).isFalse();
	}

	@Test
	@DisplayName("시작일부터 종료일까지의 일수를 계산한다")
	void 시작일부터_종료일까지의_일수를_계산한다() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 10);
		DateRange dateRange = DateRange.of(startDate, endDate);

		// when
		long dayCount = dateRange.getDayCount();

		// then
		assertThat(dayCount).isEqualTo(10);
	}

	@Test
	@DisplayName("동일한 범위를 가진 DateRange는 동등하다")
	void 동일한_범위를_가진_DateRange는_동등하다() {
		// given
		LocalDate startDate = LocalDate.of(2025, 1, 1);
		LocalDate endDate = LocalDate.of(2025, 1, 31);
		DateRange dateRange1 = DateRange.of(startDate, endDate);
		DateRange dateRange2 = DateRange.of(startDate, endDate);
		DateRange dateRange3 = DateRange.of(startDate, LocalDate.of(2025, 2, 1));

		// when & then
		assertThat(dateRange1).isEqualTo(dateRange2).isNotEqualTo(dateRange3);
		assertThat(dateRange1.hashCode()).hasSameHashCodeAs(dateRange2.hashCode());
	}
}
