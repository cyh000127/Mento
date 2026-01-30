package com.mento.domain.payment.service.query;

import org.springframework.stereotype.Service;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.PaymentException;
import com.mento.domain.payment.dto.PaymentResponseDto;
import com.mento.domain.payment.entity.Payment;
import com.mento.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentQueryService {
	private final PaymentRepository paymentRepository;

	public PaymentResponseDto findById(Long id) {
		Payment payment = paymentRepository.findById(id)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

		return PaymentResponseDto.builder()
			.paymentId(String.valueOf(payment.getPaymentId()))
			.reservationId(String.valueOf(payment.getReservation().getId()))
			.amount(String.valueOf(payment.getAmount()))
			.status(String.valueOf(payment.getStatus()))
			.paidAt(payment.getPaidAt())
			.refundedAt(payment.getRefundedAt())
			.build();
	}

}
