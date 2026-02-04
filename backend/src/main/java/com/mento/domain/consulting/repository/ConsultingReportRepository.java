package com.mento.domain.consulting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.consulting.entity.ConsultingReport;

public interface ConsultingReportRepository extends JpaRepository<ConsultingReport, Long> {
}