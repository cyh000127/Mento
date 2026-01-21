package com.mready.domain.member.service.command;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mready.common.error.ErrorCode;
import com.mready.domain.member.dto.request.MemberCreateReqDto;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.exception.MemberException;
import com.mready.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberCommandService 단위 테스트")
class MemberCommandServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private MemberCommandService memberCommandService;

	@Test
	@DisplayName("회원_생성_성공_테스트")
	void 회원_생성_성공_테스트() {
		// given
		MemberCreateReqDto request = MemberCreateReqDto.builder()
			.name("홍길동")
			.email("hong@example.com")
			.build();

		Member savedMember = Member.builder()
			.id(1L)
			.name("홍길동")
			.email("hong@example.com")
			.build();

		given(memberRepository.existsByEmail(request.email())).willReturn(false);
		given(memberRepository.save(any(Member.class))).willReturn(savedMember);

		// when
		Member result = memberCommandService.create(request);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(1L);
		assertThat(result.getName()).isEqualTo("홍길동");
		assertThat(result.getEmail()).isEqualTo("hong@example.com");

		then(memberRepository).should(times(1)).existsByEmail(request.email());
		then(memberRepository).should(times(1)).save(any(Member.class));
	}

	@Test
	@DisplayName("회원_생성_실패_이메일_중복_테스트")
	void 회원_생성_실패_이메일_중복_테스트() {
		// given
		MemberCreateReqDto request = MemberCreateReqDto.builder()
			.name("홍길동")
			.email("hong@example.com")
			.build();

		given(memberRepository.existsByEmail(request.email())).willReturn(true);

		// when & then
		assertThatThrownBy(() -> memberCommandService.create(request))
			.isInstanceOf(MemberException.class)
			.hasMessageContaining(ErrorCode.MEMBER_EMAIL_DUPLICATE.getMessage());

		then(memberRepository).should(times(1)).existsByEmail(request.email());
		then(memberRepository).should(never()).save(any(Member.class));
	}
}
