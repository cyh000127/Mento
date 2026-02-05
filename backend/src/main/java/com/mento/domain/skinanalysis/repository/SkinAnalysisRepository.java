package com.mento.domain.skinanalysis.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.skinanalysis.entity.SkinAnalysis;

public interface SkinAnalysisRepository extends JpaRepository<SkinAnalysis, Long> {
	Page<SkinAnalysis> findAllByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
