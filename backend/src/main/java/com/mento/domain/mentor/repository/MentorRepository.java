package com.mento.domain.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mento.domain.mentor.entity.Mentor;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

	@Query(value = "SELECT * FROM mentors WHERE type_id = :typeId ORDER BY RAND() LIMIT 1", nativeQuery = true)
	Mentor findRandomMentorByTypeId(@Param("typeId") Long typeId);
}