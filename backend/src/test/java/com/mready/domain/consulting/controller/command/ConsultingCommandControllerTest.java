package com.mready.domain.consulting.controller.command;

import com.mready.domain.consulting.dto.LiveKitSessionResponse;
import com.mready.domain.consulting.service.ConsultingFacadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ConsultingCommandControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConsultingFacadeService consultingFacadeService;

    @InjectMocks
    private ConsultingCommandController consultingCommandController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(consultingCommandController).build();
    }

    @Test
    @DisplayName("상담_세션_생성_응답_검증")
    void 상담_세션_생성_응답_검증() throws Exception {
        // Given
        Long timetableId = 456L;
        LiveKitSessionResponse response = LiveKitSessionResponse.of(
                timetableId,
                "livekit_token_abc123",
                "room_456",
                "wss://livekit.example.com",
                "MENTOR"
        );

        given(consultingFacadeService.createSession(eq(timetableId), any()))
                .willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/timetables/{timetableId}/sessions", timetableId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.timetableId").value(456))
                .andExpect(jsonPath("$.data.roomToken").value("livekit_token_abc123"))
                .andExpect(jsonPath("$.data.roomName").value("room_456"))
                .andExpect(jsonPath("$.data.livekitUrl").value("wss://livekit.example.com"))
                .andExpect(jsonPath("$.data.participantRole").value("MENTOR"))
                .andExpect(jsonPath("$.data.enteredAt").exists())
                .andExpect(jsonPath("$.error").isEmpty());
    }
}
