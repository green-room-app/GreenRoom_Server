package com.greenroom.modulecommon.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private final String header;

    private final String issuer;

    private final String clientSecret;

    private final int expirySeconds;

    private final int refreshExpirySeconds;
}
