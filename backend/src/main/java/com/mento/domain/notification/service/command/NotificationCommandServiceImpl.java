package com.mento.domain.notification.service.command;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.notification.converter.NotificationConverter;
import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.exception.NotificationException;
import com.mento.domain.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandServiceImpl implements NotificationCommandService {

	private final NotificationRepository notificationRepository;

	@Override
	public Notification send(final NotificationSendReqDto dto) {
		Notification notification = NotificationConverter.toEntity(dto);
		return notificationRepository.save(notification);
	}

	@Override
	public List<Notification> sendAll(final List<NotificationSendReqDto> dtos) {
		List<Notification> notifications = dtos.stream()
			.map(NotificationConverter::toEntity)
			.toList();
		return notificationRepository.saveAll(notifications);
	}

	@Override
	public void delete(final Long notificationId, final Long userId) {
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new NotificationException(ErrorCode.NOTIFICATION_NOT_FOUND));

		if (!notification.getUserId().equals(userId)) {
			throw new NotificationException(ErrorCode.ACCESS_DENIED);
		}

		notificationRepository.delete(notification);
	}
}
