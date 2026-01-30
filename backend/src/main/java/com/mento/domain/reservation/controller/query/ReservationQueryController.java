package com.mento.domain.reservation.controller.query;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.service.ReservationFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Reservation", description = "예약 관리 API")
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationQueryController {

	private final ReservationFacadeService facadeService;

	@Operation(summary = "예약 상세 정보 조회", description = "예약의 상세정보를 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<ReservationDetailResDto>> findReservationById(
		@AuthenticationPrincipal AuthenticatedUser authUser,
		@Schema(description = "예약 번호 ID") @PathVariable final Long id
	) {
		ReservationDetailResDto response = facadeService.findById(authUser, id);
		return ResponseUtils.ok(response);
	}
}