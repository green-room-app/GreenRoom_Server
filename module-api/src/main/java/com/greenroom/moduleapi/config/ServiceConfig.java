package com.greenroom.moduleapi.config;

import com.greenroom.modulecommon.config.JwtConfig;
import com.greenroom.modulecommon.jwt.JwtProvider;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public JwtProvider jwtProvider(JwtConfig jwtConfig) {
        return new JwtProvider(jwtConfig.getIssuer(),
                                jwtConfig.getClientSecret(),
                                jwtConfig.getExpirySeconds(),
                                jwtConfig.getRefreshExpirySeconds()
        );
    }
}
