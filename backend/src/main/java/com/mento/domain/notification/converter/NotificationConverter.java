package com.mento.domain.notification.converter;

import java.time.LocalDateTime;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.entity.Notification;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationConverter {

	public Notification toEntity(final NotificationSendReqDto dto, final LocalDateTime expiredAt) {
		return Notification.builder()
			.userId(dto.targetMemberId())
			.type(dto.type())
			.value(dto.value())
			.expiredAt(expiredAt)
			.build();
	}

	public NotificationResDto toNotificationResDto(final Notification entity) {
		return NotificationResDto.builder()
			.notificationId(entity.getId())
			.type(entity.getType())
			.value(entity.getValue())
			.createdAt(entity.getCreatedAt())
			.build();
	}

}
