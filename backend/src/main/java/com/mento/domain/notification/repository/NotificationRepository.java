package com.mento.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mento.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("SELECT n "
		+ "FROM Notification n "
		+ "WHERE n.userId = :userId "
		+ "ORDER BY n.createdAt DESC")
	List<Notification> findAllByUserId(@Param("userId") Long userId);

	@Query("SELECT n "
		+ "FROM Notification n "
		+ "WHERE n.userId = :userId "
		+ "AND n.expiredAt > :now "
		+ "ORDER BY n.createdAt DESC")
	List<Notification> findActiveNotifications(
		@Param("userId") Long userId,
		@Param("now") LocalDateTime now
	);
}
