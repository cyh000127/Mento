package com.mento.domain.user.service;

import com.mento.common.auth.constant.AuthConstant;
import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.user.converter.UserConverter;
import com.mento.domain.user.dto.request.UserUpdateReqDto;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.command.UserCommandService;
import com.mento.domain.user.service.query.UserQueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFacadeService {

	private final UserQueryService userQueryService;
	private final UserCommandService userCommandService;

	public UserResDto getUser(final Long id, final AuthenticatedUser authUser) {
		if (AuthConstant.ROLE_USER.equals(authUser.getRole()) && !authUser.getId().equals(id)) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		User user = userQueryService.findById(id);
		return UserConverter.toUserResDto(user);
	}

	public UserResDto updateUser(final AuthenticatedUser authUser, final UserUpdateReqDto reqDto) {
		User user = userCommandService.update(authUser.getId(), reqDto);
		return UserConverter.toUserResDto(user);
	}
}
