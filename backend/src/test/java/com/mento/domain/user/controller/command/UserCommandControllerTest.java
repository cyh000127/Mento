package com.mento.domain.user.controller.command;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.exception.handler.GlobalExceptionHandler;
import com.mento.domain.user.dto.request.UserUpdateReqDto;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.service.UserFacadeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandController 단위 테스트")
class UserCommandControllerTest {

	private MockMvc mockMvc;

	@Mock
	private UserFacadeService userFacadeService;

	@InjectMocks
	private UserCommandController userCommandController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userCommandController)
			.setControllerAdvice(new GlobalExceptionHandler())
			.setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
				@Override
				public boolean supportsParameter(MethodParameter parameter) {
					return parameter.getParameterType().equals(AuthenticatedUser.class);
				}

				@Override
				public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
					return AuthenticatedUser.builder()
						.id(1L)
						.email("test@example.com")
						.role("USER")
						.build();
				}
			})
			.build();
	}

	@Test
	@DisplayName("회원 정보 수정 성공 테스트")
	void 회원_정보_수정_성공() throws Exception {
		// given
		String content = """
			{
				"birthDate": "2000-01-01"
			}
			""";
		UserResDto resDto = UserResDto.builder()
			.id(1L)
			.email("test@example.com")
			.role(Role.USER)
			.birthDate(LocalDate.of(2000, 1, 1))
			.build();

		given(userFacadeService.updateUser(any(AuthenticatedUser.class), any(UserUpdateReqDto.class)))
			.willReturn(resDto);

		// when & then
		mockMvc.perform(patch("/api/v1/users/edit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.birthDate").value("2000-01-01"));
	}

	@Test
	@DisplayName("회원 정보 수정 실패 테스트 - 미래 날짜 입력")
	void 회원_정보_수정_실패_미래날짜() throws Exception {
		// given
		String content = """
			{
				"birthDate": "2099-01-01"
			}
			""";

		// when & then
		mockMvc.perform(patch("/api/v1/users/edit")
				.contentType(MediaType.APPLICATION_JSON)
				.content(content))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
