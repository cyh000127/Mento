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

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Consulting", description = "컨설팅 관리 API")
@RestController
@RequestMapping("/api/v1/consulting")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingQueryController {

	private final ConsultingFacadeService facadeService;

	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<SummaryInfoDto>> findConsultingReportById(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long id
	) {
		SummaryInfoDto response = facadeService.findConsultingReportById(authUser.getId(), id);
		return ResponseUtils.ok(response);
	}

}
