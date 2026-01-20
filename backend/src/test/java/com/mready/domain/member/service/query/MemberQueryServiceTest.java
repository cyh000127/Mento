package com.mready.domain.member.service.query;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mready.common.error.ErrorCode;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.exception.MemberException;
import com.mready.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberQueryService 단위 테스트")
class MemberQueryServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private MemberQueryService memberQueryService;

	@Test
	@DisplayName("회원_ID로_조회_성공_테스트")
	void 회원_ID로_조회_성공_테스트() {
		// given
		Long memberId = 1L;
		Member member = Member.builder()
			.id(memberId)
			.name("홍길동")
			.email("hong@example.com")
			.build();

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

		// when
		Member result = memberQueryService.findById(memberId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(memberId);
		assertThat(result.getName()).isEqualTo("홍길동");
		assertThat(result.getEmail()).isEqualTo("hong@example.com");

		then(memberRepository).should(times(1)).findById(memberId);
	}

	@Test
	@DisplayName("회원_ID로_조회_실패_존재하지_않는_회원_테스트")
	void 회원_ID로_조회_실패_존재하지_않는_회원_테스트() {
		// given
		Long memberId = 999L;

		given(memberRepository.findById(memberId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberQueryService.findById(memberId))
			.isInstanceOf(MemberException.class)
			.hasMessageContaining(ErrorCode.MEMBER_NOT_FOUND.getMessage());

		then(memberRepository).should(times(1)).findById(memberId);
	}
}
