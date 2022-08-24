package com.greenroom.moduleapi.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "oauth.apple")
public class AppleOAuthConfig {
    private final String authKeyUrl;   // https://appleid.apple.com/auth/keys
    private final String appBundleId;
    private final String issuer;
}
