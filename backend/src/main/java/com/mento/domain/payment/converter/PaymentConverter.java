package com.mento.domain.payment.converter;

import com.mento.domain.payment.dto.response.PaymentApproveResDto;
import com.mento.domain.payment.dto.response.PaymentInfoDto;
import com.mento.domain.payment.dto.response.PaymentReadyResDto;
import com.mento.domain.payment.entity.Payment;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentConverter {

	public PaymentReadyResDto toPaymentReadyResDto(final Payment payment, final String redirectUrl) {
		return PaymentReadyResDto.builder()
			.paymentId(payment.getId())
			.redirectUrl(redirectUrl)
			.build();
	}

	public PaymentApproveResDto toPaymentApproveResDto(final Payment payment) {
		return PaymentApproveResDto.builder()
			.paymentId(payment.getId())
			.paidAt(payment.getPaidAt())
			.build();
	}

	public PaymentInfoDto toPaymentResDto(final Payment payment) {
		if (payment == null) {
			return null;
		}

		return PaymentInfoDto.builder()
			.paymentId(payment.getId())
			.reservationId(payment.getReservation() != null ? payment.getReservation().getId() : null)
			.amount(payment.getAmount())
			.paymentMethod(payment.getPaymentMethod())
			.status(payment.getStatus())
			.paidAt(payment.getPaidAt())
			.refundedAt(payment.getRefundedAt())
			.build();
	}
}
