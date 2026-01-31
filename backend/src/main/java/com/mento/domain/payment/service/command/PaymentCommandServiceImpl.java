package com.mento.domain.payment.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.payment.dto.request.PaymentApproveReqDto;
import com.mento.domain.payment.dto.request.PaymentReadyReqDto;
import com.mento.domain.payment.dto.response.PaymentApproveResDto;
import com.mento.domain.payment.dto.response.PaymentReadyResDto;
import com.mento.domain.payment.entity.Payment;
import com.mento.domain.payment.factory.PaymentFactory;
import com.mento.domain.payment.repository.PaymentRepository;
import com.mento.domain.payment.service.command.kakao.KakaoPaymentService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCommandServiceImpl implements PaymentCommandService {

	private final PaymentRepository paymentRepository;
	private final PaymentFactory paymentFactory;
	private final KakaoPaymentService kakaoPaymentService;

	@Override
	public PaymentReadyResDto ready(final PaymentReadyReqDto request, final Long userId) {
		Payment payment = paymentFactory.createPayment(request);
		paymentRepository.save(payment);

		PaymentReadyResDto response = kakaoPaymentService.ready(payment, request, userId);
		log.info("[Payment] 결제 준비 완료 {paymentId: {}, userId: {}}", payment.getPaymentId(), userId);

		return response;
	}

	@Override
	public PaymentApproveResDto approve(final PaymentApproveReqDto request, final Long userId) {
		Payment payment = findPaymentById(request.paymentId());
		PaymentApproveResDto response = kakaoPaymentService.approve(payment, request, userId);
		log.info("[Payment] 결제 승인 완료 {paymentId: {}, userId: {}}", payment.getPaymentId(), userId);

		return response;
	}

	@Override
	public Payment save(final Payment payment) {
		Payment savedPayment = paymentRepository.save(payment);
		log.info("[Payment] 결제 정보 저장 완료 {paymentId: {}, status: {}}", savedPayment.getPaymentId(),
			savedPayment.getStatus());
		return savedPayment;
	}

	private Payment findPaymentById(final Long paymentId) {
		return paymentRepository.findById(paymentId)
			.orElseThrow(() -> {
				log.error("[Payment] 결제 정보 조회 실패 {paymentId: {}}", paymentId);
				return new IllegalArgumentException("결제 정보를 찾을 수 없습니다");
			});
	}
}
