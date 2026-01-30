package com.mento.domain.reservation.entity;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.domain.mentor.entity.Mentor;
import com.mento.domain.reservation.exception.ReservationException;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.user.entity.User;

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
@Table(name = "reservations")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reservation_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReservationStatus status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mentor_id")
	private Mentor mentor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "slot_id", nullable = false)
	private TimetableSlot slot;

	@Column(name = "survey_data", columnDefinition = "TEXT")
	private String surveyData;

	//TODO 결제 정보 연관관계 설정 필요

	public void updateSurveyData(final String surveyData) {
		this.surveyData = surveyData;
	}

	public void updateStatus(final ReservationStatus status) {
		this.status = status;
	}

	public void assignMentor(final Mentor mentor) {
		if (mentor == null) {
			throw new ReservationException(ErrorCode.MISSING_MENTOR);
		}
		this.mentor = mentor;
	}

	public void assignUser(final User user) {
		if (user == null) {
			throw new ReservationException(ErrorCode.MISSING_USER);
		}
		this.user = user;
	}

	public void assignSlot(final TimetableSlot slot) {
		if (slot == null) {
			throw new ReservationException(ErrorCode.MISSING_SLOT);
		}
		this.slot = slot;
	}
}
