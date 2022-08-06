package com.greenroom.modulecommon.jwt;

import com.greenroom.modulecommon.entity.user.OAuthType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * HTTP Request-Header 에서 JWT 값을 추출하고, JWT 값이 올바르다면 인증정보 {@link JwtAuthenticationToken}를 생성한다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

    private final JwtProvider jwtProvider;

    private final String headerKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // SecurityContextHolder 에서 인증정보를 찾을 수 없다면
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            // HTTP 요청 Header에서 JWT 값 추출
            String authorizationToken = getAuthorizationToken(request);

            if (isNotEmpty(authorizationToken)) {

                try {
                    JwtProvider.Claims claims = verify(authorizationToken);
                    log.debug("Jwt parse result: {}", claims);

                    Long id = claims.getId();
                    int oauthType = claims.getOauthType();
                    String oauthId = claims.getOauthId();

                    List<GrantedAuthority> authorities = getAuthorities(claims);

                    if (nonNull(id) && nonNull(oauthId) && authorities.size() > 0) {

                        JwtAuthentication principal = JwtAuthentication.builder()
                                .id(id)
                                .oauthType(oauthType)
                                .oauthId(oauthId)
                                .build();

                        JwtAuthenticationToken authentication = JwtAuthenticationToken.of(principal, OAuthType.from(oauthType), authorities);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }

                } catch (Exception e) {
                    log.warn("Jwt processing failed: {}", e.getMessage());
                }
            }
        }

        chain.doFilter(request, response);
    }

    private boolean canRefresh(JwtProvider.Claims claims, long refreshRangeMillis) {
        long exp = claims.exp();
        if (exp > 0) {
            long remain = exp - System.currentTimeMillis();
            return remain < refreshRangeMillis;
        }
        return false;
    }

    private List<GrantedAuthority> getAuthorities(JwtProvider.Claims claims) {
        String[] roles = claims.getRoles();
        return roles == null ? Collections.emptyList() : Arrays.stream(roles)
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
    }

    private String getAuthorizationToken(HttpServletRequest request) {
        String token = request.getHeader(headerKey);
        if (isNotEmpty(token)) {
            try {
                token = URLDecoder.decode(token, "UTF-8");
                String[] tokenParts = token.split(" ");

                if (tokenParts.length == 2) {
                    String scheme = tokenParts[0];  //BEARER 부분
                    String credentials = tokenParts[1]; //실제 토큰 부분
                    return BEARER.matcher(scheme).matches() ? credentials : null;
                }

            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }

        return null;
    }

    private JwtProvider.Claims verify(String token) {
        return jwtProvider.verify(token);
    }
}
