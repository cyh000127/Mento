package com.mento.domain.notification.service.command;

import java.util.List;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.entity.Notification;

public interface NotificationCommandService {
	Notification send(final NotificationSendReqDto dto);

	List<Notification> sendAll(final List<NotificationSendReqDto> dtos);

	void delete(final Long notificationId, final Long userId);
}
