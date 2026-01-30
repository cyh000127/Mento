package com.mento.domain.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.payment.dto.request.PaymentApproveReqDto;
import com.mento.domain.payment.dto.request.PaymentReadyReqDto;
import com.mento.domain.payment.dto.response.PaymentApproveResDto;
import com.mento.domain.payment.dto.response.PaymentReadyResDto;
import com.mento.domain.payment.dto.response.PaymentResDto;
import com.mento.domain.payment.service.command.PaymentCommandService;
import com.mento.domain.payment.service.query.PaymentQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentFacadeService {

	private final PaymentCommandService paymentCommandService;
	private final PaymentQueryService paymentQueryService;

	@Transactional
	public PaymentReadyResDto preparePayment(final PaymentReadyReqDto request, final Long userId) {
		return paymentCommandService.ready(request, userId);
	}

	@Transactional
	public PaymentApproveResDto approvePayment(final PaymentApproveReqDto request, final Long userId) {
		return paymentCommandService.approve(request, userId);
	}

	public PaymentResDto findPaymentById(final Long paymentId) {
		return paymentQueryService.findPaymentById(paymentId);
	}
}
