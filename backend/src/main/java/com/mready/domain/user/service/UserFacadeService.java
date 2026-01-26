package com.mready.domain.user.service;

import com.mready.domain.user.converter.UserConverter;
import com.mready.domain.user.dto.response.UserResDto;
import com.mready.domain.user.entity.User;
import com.mready.domain.user.service.query.UserQueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFacadeService {

	private final UserQueryService userQueryService;


	public UserResDto getUser(final Long id) {
		User user = userQueryService.findById(id);
		return UserConverter.toUserResDto(user);
	}
}
