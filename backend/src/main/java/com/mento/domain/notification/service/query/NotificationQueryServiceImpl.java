package com.mento.domain.notification.service.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
	public Slice<@NonNull NotificationResDto> getNotifications(final Long userId, final Pageable pageable) {
		return notificationRepository.findAllByUserId(userId, pageable)
			.map(NotificationConverter::toNotificationResDto);
	}
}
