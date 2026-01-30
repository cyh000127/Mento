package com.mento.domain.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.mentor.entity.Mentor;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
}