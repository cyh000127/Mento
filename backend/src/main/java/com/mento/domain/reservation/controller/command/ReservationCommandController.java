package com.mento.domain.reservation.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.livekit.dto.LiveKitSessionResponse;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.reservation.dto.request.ReservationDraftReqDto;
import com.mento.domain.reservation.dto.request.ReservationSurveyUpdateReqDto;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.dto.response.ReservationDraftResDto;
import com.mento.domain.reservation.service.ReservationFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Reservation", description = "예약 관리 API")
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationCommandController {

	private final ReservationFacadeService facadeService;

	@Operation(summary = "상담 세션 생성", description = "타임테이블 ID를 기반으로 LiveKit 상담 세션을 생성하고 토큰을 발급합니다.")
	@PostMapping("/{id}/sessions")
	public ResponseEntity<BaseResponse<LiveKitSessionResponse>> createSession(
		@PathVariable final Long id,
		@AuthenticationPrincipal final AuthenticatedUser authuser
	) {
		LiveKitSessionResponse response = facadeService.createSession(id, authuser);
		return ResponseUtils.ok(response);
	}

	@Operation(summary = "예약 초안 생성", description = "15분간 유지되는 예약 초안 데이터를 생성합니다. ")
	@PostMapping("/draft")
	public ResponseEntity<BaseResponse<ReservationDraftResDto>> createTemporaryReservation(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@RequestBody final ReservationDraftReqDto reqDto
	) {
		ReservationDraftResDto response = facadeService.createDraftReservation(authUser.getId(), reqDto.slotId());
		return ResponseUtils.created(response);
	}

	@Operation(summary = "예약 정보 수정", description = "예약의 설문 데이터를 수정합니다.")
	@PutMapping("/{id}/survey")
	public ResponseEntity<BaseResponse<ReservationDetailResDto>> updateReservationSurvey(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long id,
		@Validated @RequestBody final ReservationSurveyUpdateReqDto reqDto
	) {
		ReservationDetailResDto response = facadeService.updateReservationSurveyData(authUser, id, reqDto.surveyData());
		return ResponseUtils.ok(response);
	}
}
