package com.mento.domain.skinanalysis.controller.query;

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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/skin-analysis")
public class SkinAnalysisQueryController {

	private final SkinAnalysisFacadeService skinAnalysisFacadeService;

	@GetMapping
	public ResponseEntity<PageResponse<SkinAnalysisSummaryResDto>> getList(
		@AuthenticationPrincipal Long userId,
		@PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Page<SkinAnalysisSummaryResDto> resultPage = skinAnalysisFacadeService.getMySummaries(userId, pageable);
		return ResponseUtils.page(resultPage);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<SkinAnalysisDetailResDto>> getById(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long id
	) {
		SkinAnalysisDetailResDto response = skinAnalysisFacadeService.getById(id);
		return ResponseUtils.ok(response);
	}

}
