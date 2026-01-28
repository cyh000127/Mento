package com.mento.domain.user.controller.query;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.common.error.exception.handler.GlobalExceptionHandler;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.service.UserFacadeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
			.setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
				@Override
				public boolean supportsParameter(MethodParameter parameter) {
					return parameter.getParameterType().equals(AuthenticatedUser.class);
				}

				@Override
				public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
					NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                    String userIdParams = webRequest.getParameter("authUserId");
                    Long authUserId = userIdParams != null ? Long.valueOf(userIdParams) : 1L;
                    
					return AuthenticatedUser.builder()
						.id(authUserId)
						.email("test@example.com")
						.role(Role.USER.name())
						.build();
				}
			})
            .setControllerAdvice(new GlobalExceptionHandler()) // Exception Handler 추가
			.build();
	}

	@Test
	@DisplayName("회원 조회 성공 테스트 (본인 조회)")
	void 회원_조회_성공_테스트_본인() throws Exception {
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

		given(userFacadeService.getUser(eq(userId), any(AuthenticatedUser.class))).willReturn(response);

		// when & then
		mockMvc.perform(get("/api/v1/users/{userId}", userId)
                .param("authUserId", "1")) // 본인 ID 요청
			.andDo(MockMvcResultHandlers.print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.data.id").value(1));
	}
    
    @Test
	@DisplayName("회원 조회 실패 테스트 (타인 조회 - 403 Forbidden)")
	void 회원_조회_실패_테스트_타인() throws Exception {
		// given
		Long userId = 2L; // 타인의 ID 조회 시도

		given(userFacadeService.getUser(eq(userId), any(AuthenticatedUser.class)))
			.willThrow(new BusinessException(ErrorCode.ACCESS_DENIED));

		// when & then
		mockMvc.perform(get("/api/v1/users/{userId}", userId)
						.param("authUserId", "1")) // 로그인한 유저는 1번
				.andDo(MockMvcResultHandlers.print())
				.andExpect(result -> Assertions.assertTrue(
						result.getResolvedException() instanceof BusinessException))
				.andExpect(result -> Assertions.assertEquals(
						ErrorCode.ACCESS_DENIED,
						((BusinessException) result.getResolvedException()).getErrorCode()));
	}
}
