package com.mento.domain.notification.dto.response;

import java.time.LocalDateTime;

import com.mento.domain.notification.entity.NotificationType;

import lombok.Builder;

@Builder
public record NotificationResDto(
	Long notificationId,
	NotificationType type,
	String value,
	LocalDateTime createdAt
) {
}
