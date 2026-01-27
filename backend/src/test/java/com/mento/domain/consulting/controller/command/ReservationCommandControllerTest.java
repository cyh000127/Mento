package com.mento.domain.consulting.controller.command;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.mento.domain.consulting.dto.LiveKitSessionResponse;
import com.mento.domain.reservation.controller.command.ReservationCommandController;
import com.mento.domain.reservation.service.ReservationFacadeService;

@ExtendWith(MockitoExtension.class)
class ReservationCommandControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ReservationFacadeService reservationFacadeService;

	@InjectMocks
	private ReservationCommandController reservationCommandController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(reservationCommandController).build();
	}

	@Test
	@DisplayName("상담_세션_생성_응답_검증")
	void 상담_세션_생성_응답_검증() throws Exception {
		// Given
		Long id = 456L;
		LiveKitSessionResponse response = LiveKitSessionResponse.of(
			id,
			"livekit_token_abc123",
			"room_456",
			"wss://livekit.example.com",
			"MENTOR"
		);

		given(reservationFacadeService.createSession(eq(id), any()))
			.willReturn(response);

		// When & Then
		mockMvc.perform(post("/api/v1/reservations/{id}/sessions", id)
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
