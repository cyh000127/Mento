package com.mento.domain.payment.service.command;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.mento.common.config.properties.KakaopayProperties;
import com.mento.domain.payment.dto.KakaoReadyReqDto;
import com.mento.domain.payment.dto.KakaoReadyResDto;
import com.mento.domain.payment.dto.PaymentReqDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCommandService {
	private final RestClient kakaopayRestClient;
	private final KakaopayProperties kakaopayProperties;

	private final String orderId = UUID.randomUUID().toString();

	public KakaoReadyResDto ready(PaymentReqDto request, Long userId) {
		KakaoReadyReqDto kakaoRequest = KakaoReadyReqDto.builder()
			.cid(kakaopayProperties.cid())
			.partnerOrderId(orderId)
			.partnerUserId(String.valueOf(userId))
			.itemName(request.itemName())
			.quantity(1)
			.totalAmount(request.totalAmount())
			.taxFreeAmount(0)
			.approvalUrl(kakaopayProperties.redirectUrls().approval())
			.cancelUrl(kakaopayProperties.redirectUrls().cancel())
			.failUrl(kakaopayProperties.redirectUrls().fail())
			.build();

		KakaoReadyResDto response = kakaopayRestClient.post()
			.uri("/online/v1/payment/ready")
			.body(kakaoRequest)
			.retrieve()
			.body(KakaoReadyResDto.class);

		return response;
	}
}
