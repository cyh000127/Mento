package com.mento.common.error.exception;

import org.springframework.http.HttpStatusCode;

import com.mento.common.error.ErrorCode;

import lombok.Getter;

@Getter
public class PaymentGatewayException extends BusinessException {
	private final HttpStatusCode status;
	private final String responseBody;

	public PaymentGatewayException(HttpStatusCode status, ErrorCode errorCode, String responseBody) {
		super(errorCode);
		this.status = status;
		this.responseBody = responseBody;
	}
}