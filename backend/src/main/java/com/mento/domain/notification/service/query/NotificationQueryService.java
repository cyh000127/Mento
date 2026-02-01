package com.mento.domain.notification.service.query;

import java.util.List;

import com.mento.domain.notification.entity.Notification;

public interface NotificationQueryService {
	List<Notification> getNotifications(final Long userId);
}
