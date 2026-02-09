package com.mento.domain.skinanalysis.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.skinanalysis.entity.SkinAnalysis;
import com.mento.domain.skinanalysis.exception.SkinAnalysisException;
import com.mento.domain.skinanalysis.repository.SkinAnalysisRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SkinAnalysisQueryServiceImpl implements SkinAnalysisQueryService {

	private final SkinAnalysisRepository skinAnalysisRepository;

	@Override
	public SkinAnalysis findById(final Long id, final AuthenticatedUser authUser) {
		SkinAnalysis skinAnalysis = skinAnalysisRepository.findById(id)
			.orElseThrow(() -> new SkinAnalysisException(ErrorCode.INVALID_INPUT));

		Long ownerId = skinAnalysis.getUser() != null ? skinAnalysis.getUser().getId() : null;
		if (!authUser.isAdminOrMentor() && (ownerId == null || !ownerId.equals(authUser.getId()))) {
			log.warn("[SkinAnalysis] 접근 권한 없음 {id: {}, requestUserId: {}, ownerUserId: {}}",
				id, authUser.getId(), ownerId);
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		log.info("[SkinAnalysis] 피부 분석 상세 조회 완료 {id: {}, userId: {}}", id, ownerId);
		return skinAnalysis;
	}

	@Override
	public Page<SkinAnalysis> findAllByUserId(final Long userId, final Pageable pageable) {
		Page<SkinAnalysis> skinAnalysisList = skinAnalysisRepository.findAllByUser_IdOrderByCreatedAtDesc(
			userId,
			pageable
		);
		log.info("[SkinAnalysis] 피부 분석 목록 조회 완료 {userId: {}, total: {}, page: {}}",
			userId, skinAnalysisList.getTotalElements(), skinAnalysisList.getNumber());
		return skinAnalysisList;
	}
}
