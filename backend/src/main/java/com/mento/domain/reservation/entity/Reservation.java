package com.mento.domain.reservation.entity;

import java.time.LocalDateTime;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.common.util.TimeUtils;
import com.mento.domain.consulting.entity.ConsultingReport;
import com.mento.domain.payment.entity.Payment;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.reservation.exception.ReservationException;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.user.entity.User;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "slot_id", nullable = false)
	private TimetableSlot slot;

	@OneToOne(mappedBy = "reservation")
	Payment payment;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "consulting_report_id")
	ConsultingReport consultingReport;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mentor_id")
	private User mentor;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ReservationStatus status;

	@Column(name = "expires_at")
	private LocalDateTime expiresAt;

	@Column(name = "confirmed_at")
	private LocalDateTime confirmedAt;

	@Column(name = "survey_data", columnDefinition = "TEXT")
	private String surveyData;

	public void assignMentor(final User mentor) {
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

	public void assignPayment(final Payment payment) {
		if (payment == null) {
			throw new ReservationException(ErrorCode.MISSING_PAYMENT);
		}
		this.payment = payment;
	}

	public void assignConsultingReport(final ConsultingReport consultingReport) {
		if (consultingReport == null) {
			throw new ReservationException(ErrorCode.MISSING_CONSULTING_REPORT);
		}
		this.consultingReport = consultingReport;
	}

	public void confirm() {
		this.status = ReservationStatus.CONFIRMED;
		this.confirmedAt = TimeUtils.nowAsLocalDateTime();
		this.expiresAt = null;
	}

	public void updateSurveyData(final String surveyData) {
		this.surveyData = surveyData;
	}

	public void updateStatus(final ReservationStatus status) {
		this.status = status;
	}

	public void complete() {
		this.status = ReservationStatus.COMPLETED;
	}
}
