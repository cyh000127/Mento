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
import com.mento.domain.payment.dto.request.PaymentApproveReqDto;
import com.mento.domain.payment.dto.request.PaymentReadyReqDto;
import com.mento.domain.payment.dto.response.PaymentApproveResDto;
import com.mento.domain.payment.dto.response.PaymentReadyResDto;
import com.mento.domain.payment.service.facade.PaymentFacadeService;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Payment Command", description = "결제 명령 API")
@RestController
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@RequestMapping("/api/v1/payments")
class PaymentCommandController {

	private final PaymentFacadeService paymentFacadeService;

	@Operation(
		summary = "결제 준비",
		description = "카카오페이 결제를 준비하고 결제 페이지 리다이렉트 URL을 반환합니다. "
			+ "결제 ID와 함께 카카오페이 결제 페이지로 리다이렉트할 URL이 제공됩니다."
	)
	@PostMapping("/ready")
	public ResponseEntity<BaseResponse<PaymentReadyResDto>> ready(
		@Valid @RequestBody final PaymentReadyReqDto request,
		@AuthenticationPrincipal final AuthenticatedUser authUser
	) {
		PaymentReadyResDto response = paymentFacadeService.preparePayment(request, authUser.getId());
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "결제 승인",
		description = "카카오페이 결제를 최종 승인합니다. "
			+ "사용자가 카카오페이 결제 페이지에서 결제 완료 후 리다이렉트될 때 전달된 pg_token과 함께 호출됩니다."
	)
	@PostMapping("/approve")
	public ResponseEntity<BaseResponse<PaymentApproveResDto>> approve(
		@Valid @RequestBody final PaymentApproveReqDto request,
		@AuthenticationPrincipal final AuthenticatedUser authUser
	) {
		PaymentApproveResDto response = paymentFacadeService.approvePayment(request, authUser.getId());
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "결제 승인 및 예약 확정",
		description = "카카오페이 결제를 승인하고 동시에 예약을 확정합니다. "
			+ "멘토가 랜덤 배정되고 예약 상태가 CONFIRMED로 변경됩니다. "
			+ "이 API는 프론트에서 카카오페이 리다이렉트 후 자동으로 호출되어야 합니다."
	)
	@PostMapping("/approve/reservation")
	public ResponseEntity<BaseResponse<ReservationDetailResDto>> approveWithReservation(
		@Valid @RequestBody final PaymentApproveReqDto request,
		@AuthenticationPrincipal final AuthenticatedUser authUser
	) {
		ReservationDetailResDto response = paymentFacadeService.approvePaymentAndConfirmReservation(
			request,
			authUser.getId()
		);
		return ResponseUtils.ok(response);
	}
}
