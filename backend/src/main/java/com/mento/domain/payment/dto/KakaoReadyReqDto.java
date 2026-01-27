package com.mento.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record KakaoReadyReqDto(
	@NotNull
	@JsonProperty("cid")
	String cid,

	@NotNull
	@JsonProperty("partner_order_id")
	String partnerOrderId,

	@NotNull
	@JsonProperty("partner_user_id")
	String partnerUserId,

	@NotNull
	@JsonProperty("item_name")
	String itemName,

	@NotNull
	@JsonProperty("quantity")
	Integer quantity,

	@NotNull
	@JsonProperty("total_amount")
	Integer totalAmount,

	@NotNull
	@JsonProperty("tax_free_amount")
	Integer taxFreeAmount,

	@NotNull
	@JsonProperty("approval_url")
	String approvalUrl,

	@NotNull
	@JsonProperty("cancel_url")
	String cancelUrl,

	@NotNull
	@JsonProperty("fail_url")
	String failUrl
) {
}