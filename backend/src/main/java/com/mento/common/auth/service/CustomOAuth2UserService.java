package com.mento.common.auth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.auth.dto.OAuth2Attribute;
import com.mento.common.auth.principal.CustomOAuth2User;
import com.mento.domain.user.converter.UserConverter;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.repository.UserRepository;
import com.mento.domain.user.service.command.UserCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;
	private final UserCommandService userCommandService;

	@Override
	@Transactional
	public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Attribute attributes = OAuth2Attribute.of(registrationId, oAuth2User.getAttributes());

		UserResDto userResDto = loginOrRegister(attributes);

		User user = userRepository.findById(userResDto.id()).orElseThrow();

		return new CustomOAuth2User(user, attributes.getAttributes());
	}

	public UserResDto loginOrRegister(final OAuth2Attribute attributes) {
		return userRepository.findByEmail(attributes.getEmail())
			.map(user -> {
				if (user.getDeletedAt() != null) {
					throw new com.mento.common.error.exception.BusinessException(
						com.mento.common.error.ErrorCode.ALREADY_WITHDRAWN);
				}
				return UserConverter.toUserResDto(user);
			})
			.orElseGet(() -> {
				User newUser = UserConverter.toEntity(attributes);
				User savedUser = userCommandService.create(newUser);
				return UserConverter.toUserResDto(savedUser);
			});
	}
}
