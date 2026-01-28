package com.mento.domain.notification.service.command;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.entity.Notification;

public interface NotificationCommandService {
	Notification send(final NotificationSendReqDto dto);

	void delete(final Long notificationId, final Long userId);
}
