package com.mento.domain.payment.service.command;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.mento.common.config.properties.KakaopayProperties;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.PaymentException;
import com.mento.domain.payment.dto.KakaoApproveReqDto;
import com.mento.domain.payment.dto.KakaoApproveResDto;
import com.mento.domain.payment.dto.KakaoReadyReqDto;
import com.mento.domain.payment.dto.KakaoReadyResDto;
import com.mento.domain.payment.dto.PaymentApproveReqDto;
import com.mento.domain.payment.dto.PaymentApproveResDto;
import com.mento.domain.payment.dto.PaymentReadyReqDto;
import com.mento.domain.payment.dto.PaymentReadyResDto;
import com.mento.domain.payment.entity.Payment;
import com.mento.domain.payment.entity.PaymentMethod;
import com.mento.domain.payment.repository.PaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandService {
	private final RestClient kakaopayRestClient;
	private final KakaopayProperties kakaopayProperties;
	private final PaymentRepository paymentRepository;

	public PaymentReadyResDto ready(PaymentReadyReqDto request, Long userId) {
		Payment payment = Payment.builder()
			.reservationId(request.reservationId())
			.amount(request.totalAmount())
			.paymentMethod(PaymentMethod.KAKAO_PAY)
			.build();
		paymentRepository.save(payment);

		KakaoReadyReqDto kakaoRequest = KakaoReadyReqDto.builder()
			.cid(kakaopayProperties.cid())
			.partnerOrderId(String.valueOf(payment.getPaymentId()))
			.partnerUserId(String.valueOf(userId))
			.itemName(request.itemName())
			.quantity(1L)
			.totalAmount(request.totalAmount())
			.taxFreeAmount(0L)
			.approvalUrl(kakaopayProperties.redirectUrls().approval())
			.cancelUrl(kakaopayProperties.redirectUrls().cancel())
			.failUrl(kakaopayProperties.redirectUrls().fail())
			.build();

		KakaoReadyResDto kakaoResponse = Optional.ofNullable(
			kakaopayRestClient.post()
				.uri("/online/v1/payment/ready")
				.body(kakaoRequest)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (_, res) -> {
					String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
					log.error("KakaoPay Client Error (4xx): body={}", errorBody);
					throw new PaymentException(ErrorCode.PAYMENT_READY_FAILED);
				})
				.body(KakaoReadyResDto.class)
		).orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_READY_FAILED));
		payment.ready(kakaoResponse.tid());

		return PaymentReadyResDto.builder()
			.paymentId(String.valueOf(payment.getPaymentId()))
			.redirectUrl(kakaoResponse.nextRedirectPcUrl())
			.build();
	}

	public PaymentApproveResDto approve(PaymentApproveReqDto request, Long userId) {
		Payment payment = paymentRepository.findById(request.paymentId())
			.orElseThrow(() -> new PaymentException(ErrorCode.PAYMENT_NOT_FOUND));

		KakaoApproveReqDto kakaoRequest = KakaoApproveReqDto.builder()
			.cid(kakaopayProperties.cid())
			.tid(payment.getKakaoTid())
			.partnerOrderId(String.valueOf(request.paymentId()))
			.partnerUserId(String.valueOf(userId))
			.pgToken(request.pgToken())
			.build();

		KakaoApproveResDto kakaoResponse = kakaopayRestClient.post()
			.uri("/online/v1/payment/approve")
			.body(kakaoRequest)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, (_, res) -> {
				String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
				log.error("KakaoPay Client Error (4xx): paymentId={}, body={}", request.paymentId(), errorBody);
				payment.fail(); // TODO: payment 상태 변화 처리가 롤백 되지 않는지 검증 필요.
				throw new PaymentException(ErrorCode.PAYMENT_APPROVE_FAILED);
			})
			.onStatus(HttpStatusCode::is5xxServerError, (_, res) -> {
				String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
				log.error("KakaoPay Server Error (5xx): paymentId={}, body={}", request.paymentId(), errorBody);
				payment.fail();
				throw new PaymentException(ErrorCode.PAYMENT_APPROVE_FAILED);
			})
			.body(KakaoApproveResDto.class);

		payment.approve();

		return PaymentApproveResDto.builder()
			.paymentId(String.valueOf(payment.getPaymentId()))
			.paidAt(payment.getPaidAt())
			.build();
	}

}
