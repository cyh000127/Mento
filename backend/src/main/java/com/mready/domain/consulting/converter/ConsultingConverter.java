package com.mready.domain.consulting.converter;

import com.mready.domain.consulting.dto.LiveKitSessionResponse;
import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;

@UtilityClass
public class ConsultingConverter {

    public LiveKitSessionResponse toLiveKitSessionResponse(
            Long timetableId, 
            String roomToken, 
            String roomName, 
            String livekitUrl, 
            String participantRole) {
        
        return LiveKitSessionResponse.builder()
                .timetableId(timetableId)
                .roomToken(roomToken)
                .roomName(roomName)
                .livekitUrl(livekitUrl)
                .participantRole(participantRole)
                .enteredAt(LocalDateTime.now())
                .build();
    }
}
