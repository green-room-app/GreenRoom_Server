package com.greenroom.moduleapi.security.jwt;

import com.greenroom.moduleapi.controller.auth.AuthDto;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.Role;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import com.greenroom.modulecommon.jwt.JwtAuthenticationToken;
import com.greenroom.modulecommon.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.ClassUtils.isAssignable;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public boolean supports(Class<?> authentication) {
        return isAssignable(JwtAuthenticationToken.class, authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        return createUserAuthentication(authenticationToken.getAuthenticationId(), authenticationToken.getOAuthType());
    }

    private Authentication createUserAuthentication(String oauthId, OAuthType oAuthType) {
        try {
            User user = userService.getUserByOauthIdAndOauthType(oauthId, oAuthType);
            JwtAuthentication jwtAuthentication = JwtAuthentication.from(user);
            JwtAuthenticationToken authenticationToken =
                JwtAuthenticationToken.of(jwtAuthentication, user.getOauthType(), createAuthorityList(Role.USER.value()));

            String apiToken = jwtProvider.createApiToken(user, new String[]{Role.USER.value()});
            String refreshToken = jwtProvider.createRefreshToken();
            authenticationToken.setDetails(AuthDto.AuthResponse.of(apiToken, refreshToken));

            refreshTokenService.saveRefreshToken(user.getId(), refreshToken);

            return authenticationToken;
        } catch (ApiException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (DataAccessException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }
}
