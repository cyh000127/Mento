package com.mento.domain.user.controller.query;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.service.UserFacadeService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryControllerTest 단위 테스트")
class UserQueryControllerTest {

	private MockMvc mockMvc;

	@Mock
	private UserFacadeService userFacadeService;

	@InjectMocks
	private UserQueryController userQueryController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userQueryController)
			.build();
	}

	@Test
	@DisplayName("회원 조회 성공 테스트 (200 OK)")
	void 회원_조회_테스트() throws Exception {
		// given
		Long userId = 1L;
		UserResDto response = UserResDto.builder()
			.id(userId)
			.name("Test User")
			.email("test@example.com")
			.birthDate(LocalDate.of(1990, 1, 1))
			.role(Role.USER)
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		given(userFacadeService.getUser(userId)).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/users/{userId}", userId))
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers
				.jsonPath("$.data.id").value(1))
			.andExpect(MockMvcResultMatchers
				.jsonPath("$.data.name").value("Test User"))
			.andExpect(MockMvcResultMatchers
				.jsonPath("$.data.role").value("USER"));
	}
}
