package com.mento.domain.skin_analysis.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.skin_analysis.converter.SkinAnalysisConverter;
import com.mento.domain.skin_analysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skin_analysis.dto.response.SkinAnalysisSummaryResDto;
import com.mento.domain.skin_analysis.entity.SkinAnalysis;
import com.mento.domain.skin_analysis.repository.SkinAnalysisRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SkinAnalysisQueryServiceImpl implements SkinAnalysisQueryService {

	private final SkinAnalysisRepository skinAnalysisRepository;

	@Override
	public SkinAnalysisDetailResDto getById(Long id) {
		SkinAnalysis entity = skinAnalysisRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT));
		return SkinAnalysisConverter.toSkinAnalysisDetailResDto(entity);
	}

	@Override
	public Page<SkinAnalysisSummaryResDto> getMySummaries(Long userId, Pageable pageable) {
		return skinAnalysisRepository.findSummariesByUserId(userId, pageable);
	}

}
