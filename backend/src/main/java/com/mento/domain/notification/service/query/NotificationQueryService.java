package com.mento.domain.notification.service.query;

import java.util.List;

import com.mento.domain.notification.dto.response.NotificationResDto;

public interface NotificationQueryService {
	List<NotificationResDto> getNotifications(final Long userId);
}
