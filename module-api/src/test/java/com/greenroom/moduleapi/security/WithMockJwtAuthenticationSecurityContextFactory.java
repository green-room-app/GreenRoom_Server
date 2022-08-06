package com.greenroom.moduleapi.security;

import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import com.greenroom.modulecommon.jwt.JwtAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class WithMockJwtAuthenticationSecurityContextFactory implements WithSecurityContextFactory<WithMockJwtAuthentication> {

    @Override
    public SecurityContext createSecurityContext(WithMockJwtAuthentication annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        JwtAuthentication principal = JwtAuthentication.builder()
                .id(annotation.id())
                .oauthType(annotation.oauthType())
                .oauthId(annotation.oauthId())
                .build();

        JwtAuthenticationToken authentication = JwtAuthenticationToken.of(principal,
                OAuthType.from(principal.getOauthType()),
                createAuthorityList(annotation.role())
        );

        context.setAuthentication(authentication);
        return context;
    }
}
