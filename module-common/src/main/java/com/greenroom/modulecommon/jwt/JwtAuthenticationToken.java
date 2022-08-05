package com.greenroom.modulecommon.jwt;

import com.greenroom.modulecommon.entity.user.OAuthType;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * 인증정보(Authentication) 구현 클래스
 */
@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    /**
     * 인증 주체를 나타낸다.
     */
    private final Object principal;
    private final OAuthType oAuthType;

    public static JwtAuthenticationToken from(String principal, OAuthType oAuthType) {
        return new JwtAuthenticationToken(principal, oAuthType);
    }

    public static JwtAuthenticationToken of(Object principal, OAuthType oAuthType, Collection<? extends GrantedAuthority> authorities) {
        return new JwtAuthenticationToken(principal, oAuthType, authorities);
    }

    public String getAuthenticationId() {
        return (String)principal;
    }

    private JwtAuthenticationToken(String principal, OAuthType oAuthType) {
        super(null);
        super.setAuthenticated(false);

        this.principal = principal;
        this.oAuthType = oAuthType;
    }

    private JwtAuthenticationToken(Object principal, OAuthType oAuthType, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);

        this.principal = principal;
        this.oAuthType = oAuthType;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    // Not Used. just Override
    @Override
    public Object getCredentials() {
        return null;
    }
}
