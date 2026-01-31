package com.mento.domain.payment.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.f4b6a3.tsid.TsidCreator;
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
import jakarta.persistence.Id;
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
	@Column(name = "payment_id")
	@Builder.Default
	@JsonSerialize(using = ToStringSerializer.class)
	private Long paymentId = TsidCreator.getTsid().toLong();

	@OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
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
