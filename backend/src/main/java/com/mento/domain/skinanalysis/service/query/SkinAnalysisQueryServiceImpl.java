package com.mento.domain.skinanalysis.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.skinanalysis.converter.SkinAnalysisConverter;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisSummaryResDto;
import com.mento.domain.skinanalysis.entity.SkinAnalysis;
import com.mento.domain.skinanalysis.repository.SkinAnalysisRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly = true)
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
		Page<SkinAnalysis> entityPage = skinAnalysisRepository.findAllByUserId(userId, pageable);
		return entityPage.map(SkinAnalysisConverter::toSkinAnalysisSummaryResDto);
	}

}
