package com.mready.domain.consulting.service;

import com.mready.common.auth.principal.AuthenticatedUser;
import com.mready.domain.consulting.dto.LiveKitSessionResponse;
import com.mready.domain.consulting.service.command.ConsultingCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultingFacadeService {

    private final ConsultingCommandService consultingCommandService;

    public LiveKitSessionResponse createSession(Long timetableId, AuthenticatedUser user) {
        return consultingCommandService.createSession(timetableId, user);
    }
}
