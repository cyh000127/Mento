package com.mento.domain.payment.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.payment.dto.PaymentApproveReqDto;
import com.mento.domain.payment.dto.PaymentApproveResDto;
import com.mento.domain.payment.dto.PaymentReadyReqDto;
import com.mento.domain.payment.dto.PaymentReadyResDto;
import com.mento.domain.payment.service.command.PaymentCommandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
class PaymentCommandController {

	private final PaymentCommandService paymentCommandService;

	@PostMapping("/ready")
	public ResponseEntity<BaseResponse<PaymentReadyResDto>> ready(
		@Valid @RequestBody PaymentReadyReqDto request,
		@AuthenticationPrincipal AuthenticatedUser authUser
	) {
		PaymentReadyResDto response = paymentCommandService.ready(request, authUser.getId());
		return ResponseUtils.ok(response);
	}

	@PostMapping("/approve")
	public ResponseEntity<BaseResponse<PaymentApproveResDto>> approve(
		@Valid @RequestBody PaymentApproveReqDto request,
		@AuthenticationPrincipal AuthenticatedUser user
	) {
		PaymentApproveResDto response = paymentCommandService.approve(request, user.getId());
		return ResponseUtils.ok(response);
	}
}
