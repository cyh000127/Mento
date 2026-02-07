package com.mento.domain.notification.dto.message;

import java.time.LocalDateTime;

import com.mento.domain.notification.entity.NotificationType;

import lombok.Builder;

@Builder
public record NotificationMessage(
	Long notificationId,
	Long userId,
	NotificationType type,
	String content,
	LocalDateTime createdAt
) {
}
