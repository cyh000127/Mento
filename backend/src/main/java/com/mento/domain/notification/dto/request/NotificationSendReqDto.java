package com.mento.domain.notification.dto.request;

import java.time.LocalDateTime;

import com.mento.domain.notification.entity.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "알림 테스트 발송 요청 DTO")
public record NotificationSendReqDto(
	@Schema(description = "알림 수신 대상 회원 ID", example = "100")
	@NotNull(message = "대상 회원 ID는 필수입니다")
	Long targetMemberId,

	@Schema(description = "알림 유형", example = "RESERVATION_REMINDER")
	@NotNull(message = "알림 유형은 필수입니다")
	NotificationType type,

	@Schema(description = "알림 제목", example = "상담 예약 알림")
	String title,

	@Schema(description = "알림 내용", example = "30분 후 상담이 시작됩니다.")
	String message,

	@Schema(description = "알림 이동 URL", example = "/consulting/100")
	String url,

	@Schema(description = "알림 만료 시간", example = "2026-01-30T12:00:00")
	LocalDateTime expiredAt
) {
}
