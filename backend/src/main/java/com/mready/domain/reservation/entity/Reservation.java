package com.mready.domain.reservation.entity;

import com.mready.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "reservation")
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status; //[PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW]

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "consultant_id", nullable = false)
    private Long consultantId;

    @Column(name = "timetable_id", nullable = false)
    private Long timetableId;

    @Column(name = "payment_id")
    private Long paymentId;
}
