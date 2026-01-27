package com.mento.domain.consulting.service;

import org.springframework.stereotype.Service;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.domain.consulting.dto.LiveKitSessionResponse;
import com.mento.domain.consulting.service.command.ConsultingCommandService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultingFacadeService {

	private final ConsultingCommandService consultingCommandService;

	public LiveKitSessionResponse createSession(Long timetableId, AuthenticatedUser user) {
		return consultingCommandService.createSession(timetableId, user);
	}
}
