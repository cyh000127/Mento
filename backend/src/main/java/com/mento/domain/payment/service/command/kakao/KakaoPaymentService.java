package com.mento.domain.payment.service.command.kakao;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import com.mento.common.config.properties.KakaopayProperties;
import com.mento.common.config.restclient.KakaopayRestClientContainer;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.PaymentException;
import com.mento.domain.payment.converter.PaymentConverter;
import com.mento.domain.payment.dto.KakaoApproveReqDto;
import com.mento.domain.payment.dto.KakaoApproveResDto;
import com.mento.domain.payment.dto.KakaoReadyReqDto;
import com.mento.domain.payment.dto.KakaoReadyResDto;
import com.mento.domain.payment.dto.request.PaymentApproveReqDto;
import com.mento.domain.payment.dto.request.PaymentReadyReqDto;
import com.mento.domain.payment.dto.response.PaymentApproveResDto;
import com.mento.domain.payment.dto.response.PaymentReadyResDto;
import com.mento.domain.payment.entity.Payment;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoPaymentService {

	private final KakaopayRestClientContainer kakaopayRestClientContainer;
	private final KakaopayProperties kakaopayProperties;

	public PaymentReadyResDto ready(final Payment payment, final PaymentReadyReqDto request, final Long userId) {
		KakaoReadyReqDto kakaoRequest = buildKakaoReadyRequest(payment, request, userId);
		KakaoReadyResDto kakaoResponse = callKakaoReadyApi(kakaoRequest);

		payment.updateReady(kakaoResponse.tid());

		return PaymentConverter.toPaymentReadyResDto(payment, kakaoResponse.nextRedirectPcUrl());
	}

	public PaymentApproveResDto approve(final Payment payment, final PaymentApproveReqDto request, final Long userId) {
		KakaoApproveReqDto kakaoRequest = buildKakaoApproveRequest(payment, request, userId);
		callKakaoApproveApi(kakaoRequest, payment);

		payment.updateApprove();

		return PaymentConverter.toPaymentApproveResDto(payment);
	}

	private KakaoReadyReqDto buildKakaoReadyRequest(
		final Payment payment,
		final PaymentReadyReqDto request,
		final Long userId
	) {
		return KakaoReadyReqDto.builder()
			.cid(kakaopayProperties.cid())
			.partnerOrderId(String.valueOf(payment.getId()))
			.partnerUserId(String.valueOf(userId))
			.itemName(request.itemName())
			.quantity(1L)
			.totalAmount(request.totalAmount())
			.taxFreeAmount(0L)
			.approvalUrl(kakaopayProperties.redirectUrls().approval())
			.cancelUrl(kakaopayProperties.redirectUrls().cancel())
			.failUrl(kakaopayProperties.redirectUrls().fail())
			.build();
	}

	private KakaoApproveReqDto buildKakaoApproveRequest(
		final Payment payment,
		final PaymentApproveReqDto request,
		final Long userId
	) {
		return KakaoApproveReqDto.builder()
			.cid(kakaopayProperties.cid())
			.tid(payment.getKakaoTid())
			.partnerOrderId(String.valueOf(request.paymentId()))
			.partnerUserId(String.valueOf(userId))
			.pgToken(request.pgToken())
			.build();
	}

	private KakaoReadyResDto callKakaoReadyApi(final KakaoReadyReqDto kakaoRequest) {
		return Optional.ofNullable(
			kakaopayRestClientContainer.get().post()
				.uri("/online/v1/payment/ready")
				.body(kakaoRequest)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (_, res) -> {
					String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
					log.error("[Payment] 카카오페이 결제 준비 실패 {body: {}}", errorBody);
					throw new PaymentException(ErrorCode.PAYMENT_READY_FAILED);
				})
				.body(KakaoReadyResDto.class)
		).orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_READY_FAILED));
	}

	private KakaoApproveResDto callKakaoApproveApi(
		final KakaoApproveReqDto kakaoRequest,
		final Payment payment
	) {
		return kakaopayRestClientContainer.get().post()
			.uri("/online/v1/payment/approve")
			.body(kakaoRequest)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (_, res) -> {
				String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
				log.error("[Payment] 카카오페이 결제 승인 실패 (4xx) {paymentId: {}, body: {}}",
					payment.getId(), errorBody);
				payment.updateFail();
				throw new PaymentException(ErrorCode.PAYMENT_APPROVE_FAILED);
			})
			.onStatus(HttpStatusCode::is5xxServerError, (_, res) -> {
				String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
				log.error("[Payment] 카카오페이 결제 승인 실패 (5xx) {paymentId: {}, body: {}}",
					payment.getId(), errorBody);
				payment.updateFail();
				throw new PaymentException(ErrorCode.PAYMENT_APPROVE_FAILED);
			})
			.body(KakaoApproveResDto.class);
	}
}
