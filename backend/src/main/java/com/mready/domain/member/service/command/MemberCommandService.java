package com.mready.domain.member.service.command;

import com.mready.common.auth.redis.repository.RefreshTokenRepository;
import com.mready.common.error.ErrorCode;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.exception.MemberException;
import com.mready.domain.member.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCommandService {

	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	public Member create(final Member member) {
		Member savedMember = memberRepository.save(member);
		log.info("[Member] OAuth 가입 완료 {id: {}, email: {}}", savedMember.getId(), savedMember.getEmail());
		return savedMember;
	}

	public void withdraw(final Long id) {
		Member member = memberRepository.findById(id)
				.orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

		if (member.getDeletedAt() != null) {
			throw new MemberException(ErrorCode.ALREADY_WITHDRAWN);
		}

		member.withdraw();
		refreshTokenRepository.deleteById(String.valueOf(id));
		log.info("[Member] 탈퇴 완료 {id: {}}", member.getId());
	}
}
