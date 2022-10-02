package com.greenroom.moduleapi.security.jwt;

import com.greenroom.modulecommon.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtProvider jwtProvider;

    public Long getId(String accessToken) {
        checkArgument(isNotEmpty(accessToken), "accessToken 값은 필수입니다.");

        JwtProvider.Claims claims = jwtProvider.decode(accessToken);
        return claims.getId();
    }
}
