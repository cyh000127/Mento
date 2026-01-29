package com.mento.domain.payment.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.f4b6a3.tsid.TsidCreator;
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
	@Builder.Default
	@JsonSerialize(using = ToStringSerializer.class)
	private Long paymentId = TsidCreator.getTsid().toLong();

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

	public void approve(LocalDateTime paidAt) {
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
