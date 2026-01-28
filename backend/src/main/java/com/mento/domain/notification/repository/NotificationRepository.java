package com.mento.domain.notification.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	Slice<Notification> findByUserIdOrderByCreatedAtDesc(final Long userId, final Pageable pageable);
}
