package com.mento.domain.notification.service.query;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.notification.converter.NotificationConverter;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.repository.NotificationRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationQueryServiceImpl implements NotificationQueryService {

	private final NotificationRepository notificationRepository;

	@Override
	public List<@NonNull NotificationResDto> getNotifications(final Long userId) {
		return notificationRepository.findAllByUserId(userId)
			.stream()
			.map(NotificationConverter::toNotificationResDto)
			.toList();
	}
}
