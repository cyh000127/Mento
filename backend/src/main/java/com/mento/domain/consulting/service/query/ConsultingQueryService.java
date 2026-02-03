package com.mento.domain.consulting.service.query;

import com.mento.domain.consulting.entity.Consulting;

public interface ConsultingQueryService {

	Consulting findByRoomId(String roomId);
}
