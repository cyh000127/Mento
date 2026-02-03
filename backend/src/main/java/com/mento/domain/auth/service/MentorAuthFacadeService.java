package com.mento.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.auth.dto.Token;
import com.mento.common.auth.jwt.JwtTokenProvider;
import com.mento.domain.auth.converter.AuthConverter;
import com.mento.domain.auth.dto.request.MentorLoginReqDto;
import com.mento.domain.auth.service.command.MentorAuthCommandService;
import com.mento.domain.mentor.entity.Mentor;
import com.mento.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MentorAuthFacadeService {

	private final MentorAuthCommandService mentorAuthCommandService;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public Token login(final MentorLoginReqDto reqDto) {
		Mentor mentor = mentorAuthCommandService.login(reqDto);

		User dummyUser = AuthConverter.toUser(mentor);

		return jwtTokenProvider.createToken(dummyUser);
	}
}
