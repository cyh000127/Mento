package com.mento.domain.skinanalysis.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.skinanalysis.dto.request.SkinAnalysisReqDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.service.facade.SkinAnalysisFacadeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skin-analysis")
public class SkinAnalysisCommandController {

	private final SkinAnalysisFacadeService skinAnalysisFacadeService;

	@PostMapping
	public ResponseEntity<BaseResponse<SkinAnalysisDetailResDto>> analyze(
		@RequestBody @Valid final SkinAnalysisReqDto dto,
		@AuthenticationPrincipal final AuthenticatedUser authUser
	) {
		SkinAnalysisDetailResDto response = skinAnalysisFacadeService.analyzeSkin(authUser.getId(), dto);
		return ResponseUtils.ok(response);
	}
}
