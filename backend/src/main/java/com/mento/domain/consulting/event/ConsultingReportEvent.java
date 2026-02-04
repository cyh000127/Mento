package com.mento.domain.consulting.event;

import org.springframework.context.ApplicationEvent;

import com.mento.domain.reservation.entity.Reservation;

import lombok.Getter;

@Getter
public class ConsultingReportEvent extends ApplicationEvent {

	private final Reservation reservation;
	private final String roomId;
	private final String mentorTypeName;

	public ConsultingReportEvent(
		final Object source,
		final Reservation reservation,
		final String roomId,
		final String mentorTypeName
	) {
		super(source);
		this.reservation = reservation;
		this.roomId = roomId;
		this.mentorTypeName = mentorTypeName;
	}
}
