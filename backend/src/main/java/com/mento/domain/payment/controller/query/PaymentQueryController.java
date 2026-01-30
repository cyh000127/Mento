package com.mento.domain.payment.controller.query;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.payment.dto.PaymentResponseDto;
import com.mento.domain.payment.service.query.PaymentQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
class PaymentQueryController {

	private final PaymentQueryService paymentQueryService;

	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<PaymentResponseDto>> findById(@PathVariable Long id) {
		PaymentResponseDto responseDto = paymentQueryService.findById(id);
		return ResponseUtils.ok(responseDto);
	}
}
