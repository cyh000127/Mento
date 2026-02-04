package com.mento.domain.payment.entity;

import java.time.LocalDateTime;

import com.mento.common.converter.AesConverter;
import com.mento.common.entity.BaseEntity;
import com.mento.common.util.TimeUtils;
import com.mento.domain.reservation.entity.Reservation;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "payments")
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long id;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "reservation_id")
	private Reservation reservation;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	@Builder.Default
	private PaymentStatus status = PaymentStatus.INIT;

	@Convert(converter = AesConverter.class)
	@Column(name = "kakao_tid")
	private String kakaoTid;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@Column(name = "refunded_at")
	private LocalDateTime refundedAt;

	public void assignReservation(final Reservation reservation) {
		if (reservation == null) {
			throw new IllegalArgumentException("예약 정보가 누락되었습니다");
		}
		this.reservation = reservation;
		if (reservation.getPayment() != this) {
			reservation.assignPayment(this);
		}
	}

	public void updateReady(final String kakaoTid) {
		this.kakaoTid = kakaoTid;
		this.status = PaymentStatus.READY;
	}

	public void updateApprove() {
		this.paidAt = TimeUtils.nowAsLocalDateTime();
		this.status = PaymentStatus.PAID;
	}

	public void updateRefund() {
		this.refundedAt = TimeUtils.nowAsLocalDateTime();
		this.status = PaymentStatus.REFUNDED;
	}

	public void updateFail() {
		this.status = PaymentStatus.FAILED;
	}

}
