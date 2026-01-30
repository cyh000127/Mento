package com.mento.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record KakaoReadyReqDto(
	@JsonProperty("cid")
	String cid,

	@JsonProperty("partner_order_id")
	String partnerOrderId,

	@JsonProperty("partner_user_id")
	String partnerUserId,

	@JsonProperty("item_name")
	String itemName,

	@JsonProperty("quantity")
	Long quantity,

	@JsonProperty("total_amount")
	Long totalAmount,

	@JsonProperty("tax_free_amount")
	Long taxFreeAmount,

	@JsonProperty("approval_url")
	String approvalUrl,

	@JsonProperty("cancel_url")
	String cancelUrl,

	@JsonProperty("fail_url")
	String failUrl
) {
}