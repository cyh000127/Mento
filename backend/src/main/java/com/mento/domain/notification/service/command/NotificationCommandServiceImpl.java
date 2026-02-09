package com.mento.domain.notification.service.command;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
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
	static final long DEFAULT_EXPIRED_DAYS = 90L;


	@Override
	public Notification save(final Notification notification) {
		return notificationRepository.save(notification);
	}

	@Override
	public List<Notification> saveAll(final List<Notification> notifications) {
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
