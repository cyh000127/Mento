package com.mento.domain.skin_analysis.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.skin_analysis.entity.SkinAnalysis;

public interface SkinAnalysisRepository extends JpaRepository<SkinAnalysis, Long> {
}
