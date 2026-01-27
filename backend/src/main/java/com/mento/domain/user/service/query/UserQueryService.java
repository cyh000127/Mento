package com.mento.domain.user.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.exception.UserException;
import com.mento.domain.user.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserQueryService {

	private final UserRepository userRepository;

	public User findById(final Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));

		if (user.getDeletedAt() != null) {
			throw new UserException(ErrorCode.USER_NOT_FOUND);
		}

		log.info("[User] 조회 완료 {id: {}, email: {}}", user.getId(), user.getEmail());
		return user;
	}

}
