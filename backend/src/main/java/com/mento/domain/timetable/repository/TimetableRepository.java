package com.mento.domain.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.timetable.entity.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
}
