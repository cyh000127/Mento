package com.mento.domain.notification.converter;

import java.time.LocalDateTime;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationConverter {

	public Notification toEntity(final NotificationSendReqDto dto, final LocalDateTime expiredAt) {
		return Notification.builder()
			.userId(dto.targetMemberId())
			.type(dto.type())
			.content(dto.content())
			.expiredAt(expiredAt)
			.build();
	}

	public Notification toEntity(final Long userId, final NotificationType type, final String content,
		final LocalDateTime expiredAt) {
		return Notification.builder()
			.userId(userId)
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

}
