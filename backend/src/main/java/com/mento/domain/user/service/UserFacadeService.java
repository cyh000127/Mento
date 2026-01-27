package com.mento.domain.user.service;

import org.springframework.stereotype.Service;

import com.mento.domain.user.converter.UserConverter;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFacadeService {

	private final UserQueryService userQueryService;

	public UserResDto getUser(final Long id) {
		User user = userQueryService.findById(id);
		return UserConverter.toUserResDto(user);
	}
}
