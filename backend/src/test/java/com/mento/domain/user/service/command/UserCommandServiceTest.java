package com.mento.domain.user.service.command;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.common.error.ErrorCode;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.exception.UserException;
import com.mento.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandService 단위 테스트")
class UserCommandServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserCommandService userCommandService;

	@Test
	@DisplayName("회원_탈퇴_성공_테스트")
	void 회원_탈퇴_성공_테스트() {
		// given
		Long userId = 1L;
		User user = User.builder()
			.id(userId)
			.name("탈퇴자")
			.email("leave@example.com")
			.build();
		// 초기 상태
		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		// when
		userCommandService.withdraw(userId);

		// then
		assertThat(user.getDeletedAt()).isNotNull(); // deletedAt이 설정되었는지 확인
		then(userRepository).should(times(1)).findById(userId);
	}

	@Test
	@DisplayName("회원_탈퇴_실패_이미_탈퇴한_회원_테스트")
	void 회원_탈퇴_실패_이미_탈퇴한_회원_테스트() {
		// given
		Long userId = 1L;
		User user = User.builder()
			.id(userId)
			.name("탈퇴자")
			.email("leave@example.com")
			.build();
		user.withdraw(); // 이미 탈퇴 상태로 만듦

		given(userRepository.findById(userId)).willReturn(Optional.of(user));

		// when & then
		assertThatThrownBy(() -> userCommandService.withdraw(userId))
			.isInstanceOf(UserException.class)
			.hasMessageContaining(ErrorCode.ALREADY_WITHDRAWN.getMessage());

		then(userRepository).should(times(1)).findById(userId);
	}

	@Test
	@DisplayName("회원_탈퇴_실패_회원_없음_테스트")
	void 회원_탈퇴_실패_회원_없음_테스트() {
		// given
		Long userId = 999L;
		given(userRepository.findById(userId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> userCommandService.withdraw(userId))
			.isInstanceOf(UserException.class)
			.hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
	}
}
