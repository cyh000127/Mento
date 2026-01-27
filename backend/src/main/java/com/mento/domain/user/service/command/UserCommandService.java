package com.mento.domain.user.service.command;

import com.mento.common.error.ErrorCode;
import com.mento.domain.user.dto.request.UserUpdateReqDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.exception.UserException;
import com.mento.domain.user.repository.UserRepository;
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

	public User update(final Long id, final UserUpdateReqDto reqDto) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		if (reqDto.birthDate() != null) {
			user.updateBirthDate(reqDto.birthDate());
		}

		log.info("[User] 회원 정보 수정 완료 {id: {}, birthDate: {}}", user.getId(), reqDto.birthDate());
		return user;
	}
}
