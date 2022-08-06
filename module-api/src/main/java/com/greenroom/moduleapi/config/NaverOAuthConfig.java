package com.greenroom.moduleapi.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "oauth.naver")
public class NaverOAuthConfig {
    private final String headerKey;   // "Authorization"

    private final String headerValue; // "Bearer"

    private final String loginUrl; // "https://openapi.naver.com/v1/nid/me"

    private final String logoutUrl; //
}
