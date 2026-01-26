package com.mready.domain.timetable.repository;

import com.mready.domain.timetable.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
}
