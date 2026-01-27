package com.mento.domain.user.service;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserFacadeService 단위 테스트")
class UserFacadeServiceTest {
	@Mock
	private UserQueryService userQueryService;

	@InjectMocks
	private UserFacadeService userFacadeService;

	@Test
	@DisplayName("회원_조회_성공_본인")
	void 회원_조회_성공_본인() {
		// given
		Long userId = 1L;
		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(userId)
			.role("USER")
			.build();

		User user = User.builder()
			.id(userId)
			.name("홍길동")
			.email("hong@example.com")
			.build();

		given(userQueryService.findById(userId)).willReturn(user);

		// when
		UserResDto result = userFacadeService.getUser(userId, authUser);

		// then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(userId);
	}

	@Test
	@DisplayName("회원_조회_성공_멘토")
	void 회원_조회_성공_멘토() {
		// given
		Long targetId = 2L;
		Long mentoId = 1L;
		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(mentoId)
			.role("MENTO")
			.build();

		User user = User.builder()
			.id(targetId)
			.name("홍길동")
			.build();

		given(userQueryService.findById(targetId)).willReturn(user);

		// when
		UserResDto result = userFacadeService.getUser(targetId, authUser);

		// then
		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("회원_조회_성공_관리자")
	void 회원_조회_성공_관리자() {
		// given
		Long targetId = 2L;
		Long adminId = 1L;
		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(adminId)
			.role("ADMIN")
			.build();

		User user = User.builder()
			.id(targetId)
			.name("홍길동")
			.build();

		given(userQueryService.findById(targetId)).willReturn(user);

		// when
		UserResDto result = userFacadeService.getUser(targetId, authUser);

		// then
		assertThat(result).isNotNull();
	}

	@Test
	@DisplayName("회원_조회_실패_타인")
	void 회원_조회_실패_타인() {
		// given
		Long targetId = 2L;
		Long userId = 1L;
		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(userId)
			.role("USER")
			.build();

		// when & then
		assertThatThrownBy(() -> userFacadeService.getUser(targetId, authUser))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
	}
}
