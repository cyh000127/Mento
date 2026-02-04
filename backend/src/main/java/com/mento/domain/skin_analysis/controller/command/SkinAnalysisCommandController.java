package com.mento.domain.skin_analysis.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.skin_analysis.dto.request.SkinAnalysisReqDto;
import com.mento.domain.skin_analysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skin_analysis.service.facade.SkinAnalysisFacadeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skin-analysis")
public class SkinAnalysisCommandController {

	private final SkinAnalysisFacadeService skinAnalysisFacadeService;

	@PostMapping
	public ResponseEntity<BaseResponse<SkinAnalysisDetailResDto>> analyzeSkin(
		@RequestBody @Valid final SkinAnalysisReqDto dto,
		@AuthenticationPrincipal final AuthenticatedUser authUser
	) {
		SkinAnalysisDetailResDto response = skinAnalysisFacadeService.analyzeSkin(authUser.getId(), dto);
		return ResponseUtils.ok(response);
	}
}
