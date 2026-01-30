package com.mento.domain.payment.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record KakaoApproveResDto(
	@JsonProperty("tid")
	String tid,

	@JsonProperty("partner_order_id")
	String partnerOrderId,

	@JsonProperty("partner_user_id")
	String partnerUserId,

	@JsonProperty("amount")
	Amount amount,

	@JsonProperty("created_at")
	LocalDateTime createdAt,

	@JsonProperty("approved_at")
	LocalDateTime approvedAt

) {
	public Long totalAmount() {
		return (amount == null) ? 0 : amount.total();
	}

	private record Amount(
		@JsonProperty("total")
		Long total
	) {
	}

}
