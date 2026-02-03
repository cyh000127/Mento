package com.mento.domain.consulting.factory;

import org.springframework.stereotype.Component;

import com.mento.domain.consulting.entity.Consulting;
import com.mento.domain.reservation.constants.LiveKitConstants;

@Component
public class ConsultingFactory {

	public Consulting createConsulting(final Long reservationId) {
		return Consulting.builder()
			.roomId(LiveKitConstants.ROOM_NAME_PREFIX + reservationId)
			.build();
	}
}
