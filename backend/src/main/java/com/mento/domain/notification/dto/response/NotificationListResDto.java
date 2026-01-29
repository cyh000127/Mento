package com.mento.domain.notification.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record NotificationListResDto(
	List<NotificationItemResDto> notifications
) {
}
