package com.mento.domain.timetable.dto.response.common;

import java.time.LocalTime;

import com.mento.domain.timetable.entity.SlotStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "시간표 슬롯 상세 정보")
public record TimetableSlotInfoDto(
	@Schema(description = "시간표 ID", example = "1")
	Long timetableId,

	@Schema(description = "슬롯 ID", example = "1")
	Long slotId,

	@Schema(description = "예약 시간", example = "14:00:00")
	LocalTime scheduledTime,

	@Schema(description = "상담 가격 (원)", example = "50000")
	Integer price,

	@Schema(description = "최대 수용 인원", example = "5")
	Integer maxCapacity,

	@Schema(description = "현재 예약 인원", example = "2")
	Integer currentCapacity,

	@Schema(description = "예약 가능 인원", example = "3")
	Integer availableCapacity,

	@Schema(description = "슬롯 상태", example = "AVAILABLE")
	SlotStatus status
) {
}
