package com.mento.domain.auth.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.auth.dto.Token;
import com.mento.common.auth.jwt.JwtTokenProvider;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.AuthException;
import com.mento.domain.auth.dto.request.MentorLoginReqDto;
import com.mento.domain.mentor.entity.Mentor;
import com.mento.domain.mentor.repository.MentorRepository;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MentorAuthCommandServiceImpl implements MentorAuthCommandService {

	private final MentorRepository mentorRepository;
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	public Token login(MentorLoginReqDto reqDto) {
		Mentor mentor = mentorRepository.findByLoginId(reqDto.loginId())
			.orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

		if (!mentor.getPassword().equals(reqDto.password())) {
			throw new AuthException(ErrorCode.INVALID_PASSWORD);
		}

		User dummyUser = User.builder()
			.id(mentor.getId())
			.name(mentor.getName())
			.email(mentor.getLoginId() + "@mentor.com")
			.role(Role.MENTOR)
			.password(mentor.getPassword())
			.kakaoId("mentor_" + mentor.getId())
			.build();

		return jwtTokenProvider.createToken(dummyUser);
	}
}
