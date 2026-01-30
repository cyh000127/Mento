package com.mento.domain.payment.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.PaymentException;
import com.mento.domain.payment.converter.PaymentConverter;
import com.mento.domain.payment.dto.response.PaymentResDto;
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
		return paymentRepository.findById(id)
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));
	}

	@Override
	public PaymentResDto findPaymentById(final Long id) {
		Payment payment = findById(id);
		return PaymentConverter.toPaymentResDto(payment);
	}
}
