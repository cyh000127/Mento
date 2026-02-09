package com.mento.domain.skinanalysis.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.skinanalysis.entity.SkinAnalysis;
import com.mento.domain.skinanalysis.repository.SkinAnalysisRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SkinAnalysisCommandServiceImpl implements SkinAnalysisCommandService {

	private final SkinAnalysisRepository skinAnalysisRepository;

	@Override
	public SkinAnalysis save(final SkinAnalysis skinAnalysis) {
		SkinAnalysis savedEntity = skinAnalysisRepository.save(skinAnalysis);
		log.info("[SkinAnalysis] 피부 분석 결과 저장 완료 {id: {}, totalScore: {}, totalGrade: {}}",
			savedEntity.getId(), savedEntity.getTotalScore(), savedEntity.getTotalGrade());
		return savedEntity;
	}
}
