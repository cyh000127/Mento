package com.mento.domain.consulting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.consulting.entity.ConsultingReport;

public interface ConsultingReportRepository extends JpaRepository<ConsultingReport, Long> {
	Optional<ConsultingReport> findByReservationId(Long reservationId);
}