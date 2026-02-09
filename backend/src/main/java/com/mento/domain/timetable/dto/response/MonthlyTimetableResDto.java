package com.mento.domain.timetable.dto.response;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "월별 시간표 응답")
public record MonthlyTimetableResDto(
	@Schema(description = "조회 시작 날짜", example = "2026-01-28")
	LocalDate startDate,

	@Schema(description = "조회 종료 날짜", example = "2026-02-28")
	LocalDate endDate,

	@Schema(description = "총 일수", example = "31")
	Integer totalDays,

	@Schema(description = "멘토 유형 ID", example = "1")
	Long mentorTypeId,

	@Schema(description = "멘토 유형 이름", example = "스킨케어")
	String mentorTypeName,

	@Schema(description = "일별 시간표 목록")
	List<DailyTimetableResDto> dailyTimetables
) {
}
