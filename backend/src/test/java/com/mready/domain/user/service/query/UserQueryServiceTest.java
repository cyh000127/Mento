package com.mready.domain.user.service.query;

import com.mready.common.error.ErrorCode;
import com.mready.domain.user.entity.User;
import com.mready.domain.user.exception.UserException;
import com.mready.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryService 단위 테스트")
class UserQueryServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserQueryService userQueryService;

	@Test
	@DisplayName("회원_ID로_조회_성공_테스트")
	void 회원_ID로_조회_성공_테스트() {
		// given
		Long userId = 1L;
		User user = User.builder()
			.id(userId)
			.name("홍길동")
			.email("hong@example.com")
			.build();

		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		// when
		User result = userQueryService.findById(userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(userId);
		assertThat(result.getName()).isEqualTo("홍길동");
		assertThat(result.getEmail()).isEqualTo("hong@example.com");

		then(userRepository).should(times(1)).findById(userId);
	}

	@Test
	@DisplayName("회원_ID로_조회_실패_존재하지_않는_회원_테스트")
	void 회원_ID로_조회_실패_존재하지_않는_회원_테스트() {
		// given
		Long userId = 999L;

		given(userRepository.findById(userId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> userQueryService.findById(userId))
			.isInstanceOf(UserException.class)
			.hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());

		then(userRepository).should(times(1)).findById(userId);
	}
}
