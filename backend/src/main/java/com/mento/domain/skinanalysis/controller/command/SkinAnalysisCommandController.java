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
import com.mento.domain.skinanalysis.dto.request.SkinAnalysisClientReqDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.service.facade.SkinAnalysisFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Skin Analysis", description = "피부 분석 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skin-analysis")
public class SkinAnalysisCommandController {

	private final SkinAnalysisFacadeService skinAnalysisFacadeService;

	@Operation(summary = "피부 분석 요청", description = "이미지 URL들과 기본 정보를 통해 피부 분석을 수행합니다.")
	@PostMapping
	public ResponseEntity<BaseResponse<SkinAnalysisDetailResDto>> analyze(
		@RequestBody @Valid final SkinAnalysisClientReqDto dto,
		@AuthenticationPrincipal final AuthenticatedUser authUser
	) {
		SkinAnalysisDetailResDto response = skinAnalysisFacadeService.analyzeSkin(authUser.getId(), dto);
		return ResponseUtils.ok(response);
	}
}
