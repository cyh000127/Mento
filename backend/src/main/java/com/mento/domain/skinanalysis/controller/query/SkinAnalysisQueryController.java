package com.mento.domain.skinanalysis.controller.query;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.response.PageResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisSummaryResDto;
import com.mento.domain.skinanalysis.service.facade.SkinAnalysisFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Skin Analysis", description = "피부 분석 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skin-analysis")
public class SkinAnalysisQueryController {

	private final SkinAnalysisFacadeService skinAnalysisFacadeService;

	@Operation(summary = "내 피부 분석 목록 조회", description = "로그인한 사용자의 피부 분석 결과 목록을 페이징하여 조회합니다.")
	@GetMapping
	public ResponseEntity<PageResponse<SkinAnalysisSummaryResDto>> getList(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@ParameterObject
		@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
		final Pageable pageable
	) {
		Page<SkinAnalysisSummaryResDto> resultPage =
			skinAnalysisFacadeService.getMySummaries(authUser.getId(), pageable);
		return ResponseUtils.page(resultPage);
	}

	@Operation(summary = "피부 분석 상세 조회", description = "피부 분석 ID를 통해 상세 분석 결과를 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<SkinAnalysisDetailResDto>> getById(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long id
	) {
		SkinAnalysisDetailResDto response = skinAnalysisFacadeService.getById(id, authUser);
		return ResponseUtils.ok(response);
	}

}
