package com.greenroom.moduleapi.security;

import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeAll
    void init() {
        String issuer = "greenroom-server";
        String clientSecret = "Rel5Bjce6MajBo08qgkNgYaTuzvJe6iwnBFhsD11";
        int expirySeconds = 10;
        int refreshExpirySeconds = 2400;
        jwtProvider = new JwtProvider(issuer, clientSecret, expirySeconds, refreshExpirySeconds);
    }

    @Test
    @DisplayName("JWT_토큰을_생성하고_복호화_할수있다")
    void createTokenAndVerify() {
        //given
        JwtProvider.Claims claims = JwtProvider.Claims.of(1L,
                OAuthType.NAVER.getCode(),
                "oauthId",
                new String[]{"ROLE_USER"});

        //when
        String encodedJWT = jwtProvider.createTokenByClaims(claims);
        log.info("encodedJWT: {}", encodedJWT);

        JwtProvider.Claims decodedJWT = jwtProvider.verify(encodedJWT);
        log.info("decodedJWT: {}", decodedJWT);

        //then
        assertThat(claims.getId()).isEqualTo(decodedJWT.getId());
        assertThat(claims.getOauthType()).isEqualTo(decodedJWT.getOauthType());
        assertThat(claims.getOauthId()).isEqualTo(decodedJWT.getOauthId());
        assertArrayEquals(claims.getRoles(), decodedJWT.getRoles());
    }

    @Test
    @DisplayName("API_토큰을_리프레시_할수있다")
    void createRenewedApiToken() throws Exception {
        if (jwtProvider.getExpirySeconds() > 0) {
            //given
            JwtProvider.Claims claims = JwtProvider.Claims.of(1L,
                    OAuthType.NAVER.getCode(),
                    "oauthId",
                    new String[]{"ROLE_USER"});

            String encodedJWT = jwtProvider.createTokenByClaims(claims);
            log.info("encodedJWT: {}", encodedJWT);

            // 1초 대기 후 토큰 갱신
            sleep(1_000L);

            //when
            String renewedEncodedJWT = jwtProvider.createRenewedAccessToken(encodedJWT);
            log.info("encodedRefreshedJWT: {}", renewedEncodedJWT);

            assertThat(encodedJWT).isNotEqualTo(renewedEncodedJWT);

            JwtProvider.Claims oldJwt = jwtProvider.verify(encodedJWT);
            JwtProvider.Claims newJwt = jwtProvider.verify(renewedEncodedJWT);

            long oldExp = oldJwt.exp();
            long newExp = newJwt.exp();

            //then
            // 1초 후에 토큰을 갱신했으므로, 새로운 토큰의 만료시각이 1초 이후임
            assertThat(newExp >= oldExp + 1_000L).isEqualTo(true);

            log.info("oldJwt: {}", oldJwt);
            log.info("newJwt: {}", newJwt);
        }
    }
}