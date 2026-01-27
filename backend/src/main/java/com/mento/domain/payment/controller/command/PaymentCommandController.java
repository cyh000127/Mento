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
import com.mento.domain.payment.dto.KakaoReadyResDto;
import com.mento.domain.payment.dto.PaymentReqDto;
import com.mento.domain.payment.service.command.PaymentCommandService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
class PaymentCommandController {

	private final PaymentCommandService paymentCommandService;

	@PostMapping("/ready")
	public ResponseEntity<BaseResponse<KakaoReadyResDto>> ready(
		@Valid @RequestBody PaymentReqDto dto,
		@AuthenticationPrincipal AuthenticatedUser user
	) {
		KakaoReadyResDto response = paymentCommandService.ready(dto, user.getId());
		return ResponseUtils.ok(response);
	}

}
