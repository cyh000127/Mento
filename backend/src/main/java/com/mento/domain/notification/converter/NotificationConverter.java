package com.mento.domain.notification.converter;

import java.time.LocalDateTime;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.entity.Notification;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationConverter {

	public Notification toEntity(final NotificationSendReqDto dto) {
		return Notification.builder()
			.userId(dto.targetMemberId())
			.type(dto.type())
			.url(dto.url())
			.title(dto.title())
			.content(dto.message())
			.expiredAt(LocalDateTime.now().plusDays(90))
			.build();
	}

	public NotificationResDto toNotificationResDto(final Notification entity) {
		return NotificationResDto.builder()
			.id(entity.getId())
			.type(entity.getType())
			.url(entity.getUrl())
			.title(entity.getTitle())
			.content(entity.getContent())
			.createdAt(entity.getCreatedAt())
			.build();
	}
}
