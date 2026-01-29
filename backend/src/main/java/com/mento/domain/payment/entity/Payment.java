package com.mento.domain.payment.entity;

import java.time.LocalDateTime;

import com.mento.common.converter.AesConverter;
import com.mento.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
	@Column(name = "payment_id")
	private Long id;

	@Column(name = "reservation_id", nullable = false)
	private Long reservationId;

	@Column(name = "amount", nullable = false)
	private Integer amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private PaymentStatus status;

	@Convert(converter = AesConverter.class)
	@Column(name = "kakao_tid")
	private String kakaoTid;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@Column(name = "refunded_at")
	private LocalDateTime refundedAt;

	public void approve(String kakaoTid, LocalDateTime paidAt) {
		this.kakaoTid = kakaoTid;
		this.paidAt = paidAt;
		this.status = PaymentStatus.PAID;
	}

	public void fail() {
		this.status = PaymentStatus.FAILED;
	}

	public void cancel() {
		this.status = PaymentStatus.CANCELLED;
	}

	public void refund() {
		this.status = PaymentStatus.REFUNDED;
	}
}
