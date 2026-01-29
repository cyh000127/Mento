package com.mento.domain.notification.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mento.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("SELECT n "
		+ "FROM Notification n "
		+ "WHERE n.userId = :userId "
		+ "ORDER BY n.createdAt DESC")
	Slice<Notification> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT n "
		+ "FROM Notification n "
		+ "WHERE n.userId = :userId "
		+ "AND n.expiredAt > :now "
		+ "ORDER BY n.createdAt DESC")
	Slice<Notification> findActiveNotifications(
		@Param("userId") Long userId,
		@Param("now") LocalDateTime now,
		Pageable pageable
	);
}
