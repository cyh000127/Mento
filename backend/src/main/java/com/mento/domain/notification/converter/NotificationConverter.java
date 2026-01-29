package com.mento.domain.notification.converter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.dto.response.NotificationItemResDto;
import com.mento.domain.notification.dto.response.NotificationListResDto;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.dto.response.NotificationTestResDto;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationConverter {

	public Notification toEntity(final NotificationSendReqDto dto) {
		NotificationType type = dto.type();
		String title = Optional.ofNullable(dto.title()).orElse(type.getDefaultTitle());
		String message = Optional.ofNullable(dto.message()).orElse(type.getDefaultMessage());
		String url = Optional.ofNullable(dto.url()).orElse(type.getDefaultUrl());

		LocalDateTime expiredAt = Optional.ofNullable(dto.expiredAt())
			.orElse(LocalDateTime.now().plusDays(90));

		return Notification.builder()
			.userId(dto.targetMemberId())
			.type(type)
			.url(url)
			.title(title)
			.content(message)
			.expiredAt(expiredAt)
			.build();
	}

	public NotificationResDto toNotificationResDto(final Notification entity) {
		return NotificationResDto.builder()
			.notificationId(entity.getId())
			.type(entity.getType())
			.url(entity.getUrl())
			.title(entity.getTitle())
			.content(entity.getContent())
			.createdAt(entity.getCreatedAt())
			.build();
	}

	public NotificationListResDto toNotificationListResDto(List<Notification> notifications) {
		List<NotificationItemResDto> items = notifications.stream()
			.map(NotificationConverter::toNotificationItemResDto)
			.toList();

		return NotificationListResDto.builder()
			.notifications(items)
			.build();
	}

	private NotificationItemResDto toNotificationItemResDto(Notification entity) {
		return NotificationItemResDto.builder()
			.notificationId(entity.getId())
			.type(entity.getType().name())
			.title(entity.getTitle())
			.content(entity.getContent())
			.url(entity.getUrl())
			.createdAt(entity.getCreatedAt())
			.build();
	}

	public NotificationTestResDto toNotificationTestResDto(Notification notification) {
		return NotificationTestResDto.builder()
			.notificationId(notification.getId())
			.targetMemberId(notification.getUserId())
			.type(notification.getType())
			.status("SENT")
			.sentAt(notification.getCreatedAt())
			.build();
	}
}
