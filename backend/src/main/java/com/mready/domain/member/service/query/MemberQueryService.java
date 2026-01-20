package com.mready.domain.member.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mready.common.error.ErrorCode;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.exception.MemberException;
import com.mready.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

	private final MemberRepository memberRepository;

	public Member findById(final Long id) {
		Member member = memberRepository.findById(id)
			.orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
		log.info("[Member] 조회 완료 {id: {}, email: {}}", member.getId(), member.getEmail());
		return member;
	}
}
