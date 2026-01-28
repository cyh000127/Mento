package com.mento.domain.notification.dto.response;

import java.time.LocalDateTime;

import com.mento.domain.notification.entity.NotificationType;

import lombok.Builder;

@Builder
public record NotificationResDto(
	Long id,
	NotificationType type,
	String url,
	String title,
	String content,
	LocalDateTime createdAt,
	boolean isRead // 읽음 처리 여부를 확인하기 위해 추가 (추후 구현 시 사용)
) {
}
