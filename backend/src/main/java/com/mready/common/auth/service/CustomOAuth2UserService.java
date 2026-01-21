package com.mready.common.auth.service;

import com.mready.common.auth.dto.OAuth2Attribute;
import com.mready.common.auth.principal.CustomOAuth2User;
import com.mready.domain.member.converter.MemberConverter;
import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.repository.MemberRepository;
import com.mready.domain.member.service.MemberFacadeService;
import com.mready.domain.member.service.command.MemberCommandService;
import com.mready.domain.member.service.query.MemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberFacadeService memberFacadeService;
	private final MemberRepository memberRepository;
	private final MemberQueryService memberQueryService;
	private final MemberCommandService memberCommandService;

	@Override
	@Transactional
	public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2Attribute attributes = OAuth2Attribute.of(registrationId, oAuth2User.getAttributes());

		// 회원가입 또는 로그인 처리
		MemberResDto memberResDto = loginOrRegister(attributes);
		
		// Principal(CustomOAuth2User)을 생성하기 위해 Member 엔티티가 필요함.
		// Facade가 DTO를 반환하므로, ID를 이용해 엔티티를 다시 조회함. (추후 리팩토링 대상)
		Member member = memberRepository.findById(memberResDto.id()).orElseThrow();

		return new CustomOAuth2User(member, attributes.getAttributes());
	}


	public Optional<Member> findByProviderId(final String providerId) {
		return memberRepository.findByProviderId(providerId);
	}


	public MemberResDto loginOrRegister(final OAuth2Attribute attributes){
		return findByProviderId(attributes.getProviderId())
				.map(MemberConverter::toMemberResDto)
				.orElseGet(() -> {
					Member newMember = MemberConverter.toEntity(attributes);
					Member savedMember = memberCommandService.create(newMember);
					return MemberConverter.toMemberResDto(savedMember);
				});
	}
}
