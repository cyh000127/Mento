package com.mento.domain.consulting.event;

import java.util.List;

import org.springframework.context.ApplicationEvent;

import com.mento.domain.consulting.vo.ChatLogEntryVo;
import com.mento.domain.reservation.entity.Reservation;

import lombok.Getter;

@Getter
public class ConsultingReportEvent extends ApplicationEvent {

	private final Reservation reservation;
	private final String mentorTypeName;
	private final List<ChatLogEntryVo> chatLogs;

	public ConsultingReportEvent(
		final Object source,
		final Reservation reservation,
		final String mentorTypeName,
		final List<ChatLogEntryVo> chatLogs
	) {
		super(source);
		this.reservation = reservation;
		this.mentorTypeName = mentorTypeName;
		this.chatLogs = chatLogs;
	}
}
