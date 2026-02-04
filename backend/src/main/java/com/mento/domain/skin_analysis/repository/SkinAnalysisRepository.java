package com.mento.domain.skin_analysis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mento.domain.skin_analysis.dto.response.SkinAnalysisSummaryResDto;
import com.mento.domain.skin_analysis.entity.SkinAnalysis;

public interface SkinAnalysisRepository extends JpaRepository<SkinAnalysis, Long> {

	@Query("""
		    SELECT new com.mento.domain.skin_analysis.dto.response.SkinAnalysisSummaryResDto(
		        s.id, s.createdAt, s.totalScore, s.skinTypeSummary
		    )
		    FROM SkinAnalysis s
		    WHERE s.userId = :userId
		""")
	Page<SkinAnalysisSummaryResDto> findSummariesByUserId(@Param("userId") Long userId, Pageable pageable);
}
