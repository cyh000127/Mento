package com.mento.domain.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.mentor.entity.MentorType;

public interface MentorTypeRepository extends JpaRepository<MentorType, Long> {
}
