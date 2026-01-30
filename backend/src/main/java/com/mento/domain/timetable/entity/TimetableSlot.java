package com.mento.domain.timetable.entity;

import java.time.LocalDateTime;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.timetable.exception.TimetableException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "timetable_slots")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableSlot extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "slot_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "timetable_id", nullable = false)
	private Timetable timetable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id", nullable = false)
	private MentorType mentorType;

	@Builder.Default
	@Column(name = "max_capacity", nullable = false)
	private Integer maxCapacity = 5;

	@Builder.Default
	@Column(name = "current_capacity", nullable = false)
	private Integer currentCapacity = 0;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private SlotStatus status = SlotStatus.AVAILABLE;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	public void withdraw() {
		this.deletedAt = LocalDateTime.now();
		this.status = SlotStatus.CLOSED;
	}

	public boolean isDeleted() {
		return deletedAt != null;
	}

	public int getAvailableCapacity() {
		return maxCapacity - currentCapacity;
	}

	public void increaseCapacity() {
		this.currentCapacity++;
		if (this.currentCapacity >= this.maxCapacity) {
			this.status = SlotStatus.FULL;
		}
	}

	public void decreaseCapacity() {
		if (this.currentCapacity > 0) {
			this.currentCapacity--;
			this.status = SlotStatus.AVAILABLE;
		}
	}

	public boolean isAvailable() {
		return status == SlotStatus.AVAILABLE && currentCapacity < maxCapacity;
	}

	public void assignTimetable(final Timetable timetable) {
		if (timetable == null) {
			throw new TimetableException(ErrorCode.MISSING_TIMETABLE);
		}
		if (this.timetable != null) {
			this.timetable.getSlots().remove(this);
		}

		this.timetable = timetable;
		if (!timetable.getSlots().contains(this)) {
			timetable.getSlots().add(this);
		}
	}

	public void assignMentorType(final MentorType mentorType) {
		if (mentorType == null) {
			throw new TimetableException(ErrorCode.MISSING_MENTOR_TYPE);
		}

		if (this.mentorType != null) {
			this.mentorType.getSlots().remove(this);
		}

		this.mentorType = mentorType;
		if (!mentorType.getSlots().contains(this)) {
			mentorType.getSlots().add(this);
		}
	}
}
