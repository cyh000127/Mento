package com.mento.domain.reservation.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.livekit.dto.LiveKitSessionResponse;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.reservation.service.ReservationFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Reservation", description = "예약 관리 API")
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationCommandController {

	private final ReservationFacadeService reservationFacadeService;

	@Operation(summary = "상담 세션 생성", description = "타임테이블 ID를 기반으로 LiveKit 상담 세션을 생성하고 토큰을 발급합니다.")
	@PostMapping("/{id}/sessions")
	public ResponseEntity<BaseResponse<LiveKitSessionResponse>> createSession(
		@PathVariable final Long id,
		@AuthenticationPrincipal final AuthenticatedUser user
	) {
		LiveKitSessionResponse response = reservationFacadeService.createSession(id, user);
		return ResponseUtils.ok(response);
	}
}
