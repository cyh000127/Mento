package com.mento.domain.timetable.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.mento.domain.timetable.dto.response.common.TimetableSlotInfoDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "일별 시간표 응답")
public record DailyTimetableResDto(
	@Schema(description = "날짜", example = "2026-01-28")
	LocalDate date,

	@Schema(description = "해당 날짜의 시간표 슬롯 목록")
	List<TimetableSlotInfoDto> slots
) {
}
