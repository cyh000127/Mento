package com.mento.domain.notification.service.command;

import java.util.List;

import com.mento.domain.notification.entity.Notification;

public interface NotificationCommandService {
	Notification save(final Notification notification);

	List<Notification> saveAll(final List<Notification> notifications);

	void delete(final Long notificationId, final Long userId);
}
