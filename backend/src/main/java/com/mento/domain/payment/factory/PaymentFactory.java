package com.mento.domain.payment.factory;

import org.springframework.stereotype.Component;

import com.mento.domain.payment.dto.request.PaymentReadyReqDto;
import com.mento.domain.payment.entity.Payment;
import com.mento.domain.payment.entity.PaymentMethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PaymentFactory {

	public Payment createPayment(final PaymentReadyReqDto request) {
		return Payment.builder()
			.amount(request.totalAmount())
			.paymentMethod(PaymentMethod.KAKAO_PAY)
			.build();
	}
}
