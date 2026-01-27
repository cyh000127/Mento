package com.mento.domain.user.service;

import com.mento.common.auth.constant.AuthConstant;
import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.user.converter.UserConverter;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFacadeService {

	private final UserQueryService userQueryService;

	public UserResDto getUser(final Long id, final AuthenticatedUser authUser) {
		if (AuthConstant.ROLE_USER.equals(authUser.getRole()) && !authUser.getId().equals(id)) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		User user = userQueryService.findById(id);
		return UserConverter.toUserResDto(user);
	}
}
