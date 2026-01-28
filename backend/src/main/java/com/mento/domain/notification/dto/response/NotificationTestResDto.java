package com.mento.domain.notification.dto.response;

import java.time.LocalDateTime;

import com.mento.domain.notification.entity.NotificationType;

import lombok.Builder;

@Builder
public record NotificationTestResDto(
	Long notificationId,
	Long targetMemberId,
	NotificationType type,
	String status,
	LocalDateTime sentAt
) {
}
