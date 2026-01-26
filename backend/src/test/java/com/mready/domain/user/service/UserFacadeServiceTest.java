package com.mready.domain.user.service;

import com.mready.domain.user.dto.response.UserResDto;
import com.mready.domain.user.entity.User;
import com.mready.domain.user.service.command.UserCommandService;
import com.mready.domain.user.service.query.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserFacadeService 단위 테스트")
class UserFacadeServiceTest {

	@Mock
	private UserCommandService userCommandService;

	@Mock
	private UserQueryService userQueryService;

	@InjectMocks
	private UserFacadeService userFacadeService;

	@Test
	@DisplayName("회원_조회_및_DTO_변환_성공_테스트")
	void 회원_조회_및_DTO_변환_성공_테스트() {
		// given
		Long userId = 1L;
		User user = User.builder()
				.id(userId)
				.name("홍길동")
				.email("hong@example.com")
				.build();

		given(userQueryService.findById(userId)).willReturn(user);

		// when
		UserResDto result = userFacadeService.getUser(userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.id()).isEqualTo(userId);
		assertThat(result.name()).isEqualTo("홍길동");
		assertThat(result.email()).isEqualTo("hong@example.com");

		then(userQueryService).should(times(1)).findById(userId);
	}
}
