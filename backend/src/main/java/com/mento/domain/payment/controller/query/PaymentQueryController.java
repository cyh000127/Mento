package com.mento.domain.payment.controller.query;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.payment.dto.response.PaymentInfoDto;
import com.mento.domain.payment.service.facade.PaymentFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment", description = "결제 조회 API")
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/api/v1/payments")
class PaymentQueryController {

	private final PaymentFacadeService paymentFacadeService;

	@Operation(
		summary = "결제 상세 조회",
		description = "결제 ID로 결제 정보를 상세 조회합니다. "
			+ "결제 금액, 결제 수단, 결제 상태, 예약 정보 등을 포함합니다."
	)
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<PaymentInfoDto>> findById(
		@Schema(description = "결제 ID", example = "1234567890123456")
		@PathVariable final Long id
	) {
		PaymentInfoDto response = paymentFacadeService.findPaymentById(id);
		return ResponseUtils.ok(response);
	}
}
