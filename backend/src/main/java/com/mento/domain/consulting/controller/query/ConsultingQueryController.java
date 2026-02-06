package com.mento.domain.consulting.controller.query;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.consulting.dto.common.SummaryInfoDto;
import com.mento.domain.consulting.service.facade.ConsultingFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Consulting", description = "상담 관리 API")
@RestController
@RequestMapping("/api/v1/consulting")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingQueryController {

	private final ConsultingFacadeService facadeService;

	@Operation(
		summary = "상담 보고서 조회",
		description = "보고서 ID로 특정 상담 보고서의 요약 정보를 조회합니다. 보고서 내용과 관련 미디어 URL 목록을 반환합니다."
	)
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<SummaryInfoDto>> findConsultingReportById(
		@Parameter(hidden = true) @AuthenticationPrincipal final AuthenticatedUser authUser,
		@Parameter(description = "조회할 보고서 ID", required = true, example = "1")
		@PathVariable final Long id
	) {
		SummaryInfoDto response = facadeService.findConsultingReportById(authUser.getId(), id);
		return ResponseUtils.ok(response);
	}

}
