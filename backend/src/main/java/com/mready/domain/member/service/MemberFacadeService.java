package com.mready.domain.member.service;

import org.springframework.stereotype.Service;

import com.mready.domain.member.converter.MemberConverter;
import com.mready.domain.member.dto.request.MemberCreateReqDto;
import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.service.command.MemberCommandService;
import com.mready.domain.member.service.query.MemberQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberFacadeService {

	private final MemberCommandService memberCommandService;
	private final MemberQueryService memberQueryService;

	public MemberResDto createMember(final MemberCreateReqDto dto) {
		Member member = memberCommandService.create(dto);
		return MemberConverter.toMemberResDto(member);
	}

	public MemberResDto getMember(final Long id) {
		Member member = memberQueryService.findById(id);
		return MemberConverter.toMemberResDto(member);
	}
}
