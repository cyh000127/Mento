package com.mento.domain.payment.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record KakaoReadyResDto(
	@JsonProperty("tid")
	String tid,

	@JsonProperty("next_redirect_app_url")
	String nextRedirectAppUrl,

	@JsonProperty("next_redirect_mobile_url")
	String nextRedirectMobileUrl,

	@JsonProperty("next_redirect_pc_url")
	String nextRedirectPcUrl,

	@JsonProperty("created_at")
	LocalDateTime createdAt
) {
}