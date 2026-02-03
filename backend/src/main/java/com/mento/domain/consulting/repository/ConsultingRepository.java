package com.mento.domain.consulting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.consulting.entity.Consulting;

public interface ConsultingRepository extends JpaRepository<Consulting, Long> {
	Optional<Consulting> findByRoomId(final String roomId);
}