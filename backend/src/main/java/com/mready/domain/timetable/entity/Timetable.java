package com.mready.domain.timetable.entity;

import com.mready.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "timetables")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timetable extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long id;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time", nullable = false)
    private LocalTime scheduledTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TimetableStatus status = TimetableStatus.ACTIVE; // [ACTIVE, INACTIVE, FULL]

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity = 15;

    @Column(name = "current_capacity", nullable = false)
    private Integer currentCapacity = 0;
}
