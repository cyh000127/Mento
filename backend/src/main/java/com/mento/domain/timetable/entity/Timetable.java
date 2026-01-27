package com.mento.domain.timetable.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.mento.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "timetables")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timetable extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "timetable_id")
	private Long id;

	@Column(name = "scheduled_date", nullable = false)
	private LocalDate scheduledDate;

	@Column(name = "scheduled_time", nullable = false)
	private LocalTime scheduledTime;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private TimetableStatus status = TimetableStatus.ACTIVE;

	@Builder.Default
	@Column(name = "max_capacity", nullable = false)
	private Integer maxCapacity = 15;

	@Builder.Default
	@Column(name = "current_capacity", nullable = false)
	private Integer currentCapacity = 0;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void withdraw() {
		this.deletedAt = LocalDateTime.now();
	}
}

