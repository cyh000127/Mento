package com.mento.domain.notification.service.query;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

	private final NotificationRepository notificationRepository;

	@Override
	public List<Notification> getNotifications(final Long userId) {
		return notificationRepository.findAllByUserId(userId);
	}

	@Override
	public List<Notification> findActiveNotifications(final Long userId, final LocalDateTime now) {
		return notificationRepository.findActiveNotifications(userId, now);
	}
}
