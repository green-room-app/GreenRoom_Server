package com.greenroom.moduleapi.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJwtAuthenticationSecurityContextFactory.class)
public @interface WithMockJwtAuthentication {

    long id() default 1L;

    int oauthType() default 1;

    String oauthId() default "oauthId";

    String role() default "ROLE_USER";
}
