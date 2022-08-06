package com.greenroom.moduleapi.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOAuthConfig {
    private final String headerKey;   // "Authorization"

    private final String headerValue; // "Bearer"

    private final String loginUrl; // "https://kapi.kakao.com/v2/user/me"

    private final String logoutUrl; // "https://kapi.kakao.com/v1/user/logout
}
