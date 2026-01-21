package com.mready.domain.member.service.command;

import com.mready.common.error.ErrorCode;
import com.mready.domain.member.converter.MemberConverter;
import com.mready.domain.member.dto.request.MemberCreateReqDto;
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

	public Member create(final MemberCreateReqDto dto) {
		if (memberRepository.existsByEmail(dto.email())) {
			throw new MemberException(ErrorCode.MEMBER_EMAIL_DUPLICATE);
		}

		Member member = MemberConverter.toEntity(dto);
		Member savedMember = memberRepository.save(member);
		log.info("[Member] 생성 완료 {id: {}, email: {}}", savedMember.getId(), savedMember.getEmail());
		return savedMember;
	}

	public Member create(final Member member) {
		Member savedMember = memberRepository.save(member);
		log.info("[Member] OAuth 가입 완료 {id: {}, email: {}, provider: {}}", savedMember.getId(), savedMember.getEmail(), savedMember.getProvider());
		return savedMember;
	}
}
