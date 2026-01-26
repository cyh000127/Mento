package com.mready.domain.user.service.command;

import com.mready.common.error.ErrorCode;
import com.mready.domain.user.entity.User;
import com.mready.domain.user.exception.UserException;
import com.mready.domain.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCommandService {

	private final UserRepository userRepository;

	public User create(final User user) {
		User savedUser = userRepository.save(user);
		log.info("[User] OAuth 가입 완료 {id: {}, email: {}}", savedUser.getId(), savedUser.getEmail());
		return savedUser;
	}


	public void withdraw(final Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		if (user.getDeletedAt() != null) {
			throw new UserException(ErrorCode.ALREADY_WITHDRAWN);
		}

		user.withdraw();
		log.info("[User] 탈퇴 완료 {id: {}}", user.getId());
	}
}
