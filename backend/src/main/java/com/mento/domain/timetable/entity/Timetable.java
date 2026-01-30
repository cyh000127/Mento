package com.mento.domain.timetable.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.domain.timetable.exception.TimetableException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
	@OneToMany(mappedBy = "timetable", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TimetableSlot> slots = new ArrayList<>();

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void withdraw() {
		this.deletedAt = LocalDateTime.now();
		slots.forEach(TimetableSlot::withdraw);
	}

	public boolean isDeleted() {
		return deletedAt != null;
	}

	public void addSlot(final TimetableSlot slot) {
		if (slot == null) {
			throw new TimetableException(ErrorCode.MISSING_SLOT);
		}
		this.slots.add(slot);
		if (slot.getTimetable() != this) {
			slot.assignTimetable(this);
		}
	}

	public void removeSlot(final TimetableSlot slot) {
		if (slot == null) {
			return;
		}
		this.slots.remove(slot);
	}
}

