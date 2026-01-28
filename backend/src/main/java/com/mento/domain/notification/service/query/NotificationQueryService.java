package com.mento.domain.notification.service.query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.mento.domain.notification.dto.response.NotificationResDto;

public interface NotificationQueryService {
	Slice<NotificationResDto> getNotifications(final Long userId, final Pageable pageable);
}
