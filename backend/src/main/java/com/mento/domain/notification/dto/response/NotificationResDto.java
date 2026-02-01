package com.mento.domain.notification.dto.response;

import java.time.LocalDateTime;

import com.mento.domain.notification.entity.NotificationType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "알림 정보 응답")
public record NotificationResDto(
	@Schema(description = "알림 ID", example = "1")
	Long notificationId,

	@Schema(description = "알림 유형", example = "MENTORING_REQUEST")
	NotificationType type,

	@Schema(description = "알림 내용", example = "30(분) 15(개) 등")
	String content,

	@Schema(description = "알림 생성 일시", example = "2024-02-01T12:00:00")
	LocalDateTime createdAt
) {
}
