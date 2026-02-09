package com.mento.common.test;

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

@Tag(name = "Test - Reservation", description = "예약 테스트용 API (인증 없이 예약 ID로 직접 접근)")
@RestController
@RequestMapping("/test/v1/reservations")
@RequiredArgsConstructor
public class LiveKitTestController {

	private final ReservationFacadeService facadeService;

	@Operation(summary = "[테스트] LiveKit 세션 생성 (무제한)", description = "유효기간이 없는 LiveKit 세션 토큰을 발급합니다.")
	@PostMapping("/{id}/sessions")
	public ResponseEntity<BaseResponse<LiveKitSessionResponse>> createInfiniteSession(
		@PathVariable final Long id,
		@AuthenticationPrincipal final AuthenticatedUser authUser
	) {
		LiveKitSessionResponse response = facadeService.createInfiniteSession(id, authUser);
		return ResponseUtils.ok(response);
	}
}
