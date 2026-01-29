package com.mento.domain.notification.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record NotificationItemResDto(
	Long notificationId,
	String type,
	String title,
	String content,
	String url,
	LocalDateTime createdAt
) {
}
