package com.mento.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record KakaoApproveReqDto(
	@NotNull
	@JsonProperty("cid")
	String cid,

	@NotNull
	@JsonProperty("tid")
	String tid,

	@NotNull
	@JsonProperty("partner_order_id")
	String partnerOrderId,

	@NotNull
	@JsonProperty("partner_user_id")
	String partnerUserId,

	@NotNull
	@JsonProperty("pg_token")
	String pgToken
) {
}
