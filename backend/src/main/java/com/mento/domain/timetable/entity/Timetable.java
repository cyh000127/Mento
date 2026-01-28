package com.mento.domain.timetable.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.mento.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void withdraw() {
		this.deletedAt = LocalDateTime.now();
		this.status = TimetableStatus.INACTIVE;
	}

	public boolean isDeleted() {
		return deletedAt != null;
	}
}

