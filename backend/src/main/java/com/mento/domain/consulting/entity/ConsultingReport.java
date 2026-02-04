package com.mento.domain.consulting.entity;

import com.mento.common.entity.BaseEntity;
import com.mento.common.error.ErrorCode;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.exception.ReservationException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "consulting_reports")
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingReport extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(mappedBy = "consultingReport")
	private Reservation reservation;

	@Column(name = "content", columnDefinition = "longtext")
	private String content;

	@Column(name = "media_url")
	private String mediaUrl;

	public void assignReservation(final Reservation reservation) {
		if (reservation == null) {
			throw new ReservationException(ErrorCode.MISSING_RESERVATION);
		}
		this.reservation = reservation;
	}

}
