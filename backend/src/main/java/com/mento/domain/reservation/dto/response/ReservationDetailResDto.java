package com.mento.domain.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.mento.domain.mentor.dto.common.MentoTypeInfoDto;
import com.mento.domain.mentor.dto.common.MentorInfoDto;
import com.mento.domain.reservation.dto.common.ReservationSurveyDto;
import com.mento.domain.user.dto.common.UserInfoDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "예약 상세 정보 응답 DTO")
public record ReservationDetailResDto(
	@Schema(description = "예약 ID", example = "1")
	Long reservationId,

	@Schema(description = "예약자 정보")
	UserInfoDto userInfo,

	@Schema(description = "멘토 정보")
	MentorInfoDto mentorInfo,

	@Schema(description = "멘토링 타입 정보")
	MentoTypeInfoDto mentorTypeInfo,

	@Schema(description = "타임테이블 ID", example = "10")
	Long timetableId,

	@Schema(description = "예약 날짜", example = "2025-01-29")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate scheduledDate,

	@Schema(description = "예약 시간", example = "09:00")
	String scheduledTime,

	@Schema(description = "예약 상태", example = "RESERVED")
	String reservationStatus,

	@Schema(description = "설문 정보")
	ReservationSurveyDto surveyInfo,

	@Schema(description = "결제 정보 ID")
	Long paymentId,

	@Schema(description = "예약 생성일시", example = "2025-01-29T10:00:00")
	LocalDateTime createdAt,

	@Schema(description = "예약 수정일시", example = "2025-01-29T10:00:00")
	LocalDateTime updatedAt
) {
}