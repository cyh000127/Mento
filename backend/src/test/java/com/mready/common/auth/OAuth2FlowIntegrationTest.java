package com.mready.common.auth;

import com.mready.common.auth.constant.AuthConstant;
import com.mready.common.auth.dto.OAuth2Attribute;
import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.common.auth.redis.RefreshToken;
import com.mready.common.auth.handler.OAuth2LoginSuccessHandler;
import com.mready.common.auth.principal.CustomOAuth2User;
import com.mready.common.auth.redis.repository.RefreshTokenRepository;
import com.mready.common.auth.service.CustomOAuth2UserService;
import com.mready.domain.member.entity.Member;
import com.mready.domain.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(properties = {
        "jwt.secret=thisIsASampleSecretKeyForTest123456789012345678901234567890",
        "jwt.access-token-expiration=3600000",
        "jwt.refresh-token-expiration=86400000"
})
@Transactional
@DisplayName("회원가입부터_로그인_플로우_통합테스트")
class OAuth2FlowIntegrationTest {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    // 회원가입 -> 로그인 -> 토큰 발급 -> 재로그인
    @Test
    @DisplayName("OAuth2_로그인_흐름_통합_테스트")
    void OAuth2_로그인_흐름_통합_테스트() throws Exception {
        Map<String, Object>  attributes = new HashMap<>();
        Map<String, Object> kakaoAccount = new HashMap<>();

        attributes.put("id", 123456789L);

        kakaoAccount.put("email", "flowtest@example.com");
        kakaoAccount.put("name", "Flow Tester");
        kakaoAccount.put("birthyear", "1990");
        kakaoAccount.put("birthday", "1225");
        attributes.put("kakao_account", kakaoAccount);

        // OAuth2Attribute 생성
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(OAuth2Attribute.KAKAO, attributes);

        // 신규 회원 가입
        MemberResDto memberResDto = customOAuth2UserService.loginOrRegister(oAuth2Attribute);

        // 회원 생성 검증
        assertThat(memberResDto).isNotNull();
        assertThat(memberResDto.email()).isEqualTo("flowtest@example.com");

        Optional<Member> memberOptional = memberRepository.findByEmail("flowtest@example.com");
        assertThat(memberOptional).isPresent();
        Member member = memberOptional.get();
        assertThat(member.getKakaoId()).isEqualTo("kakao_123456789");
        assertThat(member.getBirthDate()).isEqualTo(LocalDate.of(1990, 12, 25));

        // CustomOAuth2User 생성
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(member, attributes);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                customOAuth2User,
                null,
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name()))
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 핸들러 호출
        oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response, authentication);

        // 토큰 검증
        // Refresh Token
        Cookie refreshTokenCookie = response.getCookie("refreshToken");
        assertThat(refreshTokenCookie).isNotNull();
        String refreshTokenValue = refreshTokenCookie.getValue();
        assertThat(refreshTokenValue).isNotEmpty();

        // Refresh Token
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        Mockito.verify(refreshTokenRepository, Mockito.atLeastOnce()).save(captor.capture());
        
        RefreshToken savedToken = captor.getAllValues().get(0);
        assertThat(savedToken.getId()).isEqualTo(String.valueOf(member.getId()));
        assertThat(savedToken.getToken()).isEqualTo(refreshTokenValue);

        // Access Token (헤더)
        String authHeader = response.getHeader(AuthConstant.AUTHORIZATION);
        assertThat(authHeader)
                .isNotNull()
                .startsWith(AuthConstant.BEARER);

        // 토큰 중복 방지를 위한 1초 대기
        Thread.sleep(1000);

        // 이전 호출 기록 초기화 (중요)
        Mockito.clearInvocations(refreshTokenRepository);

        // 재로그인
        MemberResDto loginMemberResDto = customOAuth2UserService.loginOrRegister(oAuth2Attribute);
        
        // Member ID 동일 여부 확인
        assertThat(loginMemberResDto.id()).isEqualTo(member.getId());

        // 핸들러 동작 재시뮬
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        oAuth2LoginSuccessHandler.onAuthenticationSuccess(request, response2, authentication);

        // 새로운 토큰 검증
        Cookie newRefreshTokenCookie = response2.getCookie("refreshToken");
        assertThat(newRefreshTokenCookie).isNotNull();
        String newRefreshTokenValue = newRefreshTokenCookie.getValue();
        
        // 새로운 토큰으로 다시 저장되었는지 검증 (초기화했으므로 1번만 호출되어야 함)
        ArgumentCaptor<RefreshToken> newCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        Mockito.verify(refreshTokenRepository, Mockito.times(1)).save(newCaptor.capture());
        
        RefreshToken latestsavedToken = newCaptor.getValue();
        assertThat(latestsavedToken.getToken()).isEqualTo(newRefreshTokenValue);
        assertThat(latestsavedToken.getToken()).isNotEqualTo(savedToken.getToken());
    }
}