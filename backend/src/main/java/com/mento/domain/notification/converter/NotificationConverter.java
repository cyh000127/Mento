package com.mento.domain.notification.converter;

import java.time.LocalDateTime;

import com.mento.domain.notification.dto.message.NotificationMessage;
import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.user.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationConverter {

	public Notification toEntity(final NotificationSendReqDto dto, final User user, final LocalDateTime expiredAt) {
		return Notification.builder()
			.user(user)
			.type(dto.type())
			.content(dto.content())
			.expiredAt(expiredAt)
			.build();
	}

	public Notification toEntity(final User user, final NotificationType type, final LocalDateTime expiredAt) {
		return Notification.builder()
			.user(user)
			.type(type)
			.expiredAt(expiredAt)
			.build();
	}

	public Notification toEntity(final User user, final NotificationType type, final String content,
		final LocalDateTime expiredAt) {
		return Notification.builder()
			.user(user)
			.type(type)
			.content(content)
			.expiredAt(expiredAt)
			.build();
	}

	public NotificationResDto toNotificationResDto(final Notification entity) {
		return NotificationResDto.builder()
			.notificationId(entity.getId())
			.type(entity.getType())
			.content(entity.getContent())
			.createdAt(entity.getCreatedAt())
			.build();
	}

	public NotificationMessage toMessage(final Notification entity) {
		return NotificationMessage.builder()
			.notificationId(entity.getId())
			.userId(entity.getUserId())
			.type(entity.getType())
			.content(entity.getContent())
			.createdAt(entity.getCreatedAt())
			.build();
	}

	public NotificationResDto toNotificationResDto(final NotificationMessage message) {
		return NotificationResDto.builder()
			.notificationId(message.notificationId())
			.type(message.type())
			.content(message.content())
			.createdAt(message.createdAt())
			.build();
	}

}
