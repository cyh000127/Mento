package com.mento.domain.payment.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.PaymentException;
import com.mento.domain.payment.entity.Payment;
import com.mento.domain.payment.repository.PaymentRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentQueryServiceImpl implements PaymentQueryService {

	private final PaymentRepository paymentRepository;

	@Override
	public Payment findById(final Long id) {
		Payment payment = paymentRepository.findById(id)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		log.info("[Payment] 결제 조회 완료 {id: {}}", id);
		return payment;
	}

	@Override
	public Payment findDetailsById(final Long id) {
		Payment payment = paymentRepository.findDetailsById(id)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
		log.info("[Payment] 결제 상세 조회 완료 {id: {}, reservationId: {}}", id, payment.getReservation().getId());
		return payment;
	}
}
