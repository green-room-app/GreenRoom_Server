package com.greenroom.modulecommon.jwt;

import com.greenroom.modulecommon.entity.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * '인증된 사용자' 의미
 */
@Getter
@Builder
@RequiredArgsConstructor
public class JwtAuthentication {
    private final Long id;

    private final int oauthType;

    private final String oauthId;

    public static JwtAuthentication from(User user) {
        return JwtAuthentication.builder()
                .id(user.getId())
                .oauthType(user.getOauthType().getCode())
                .oauthId(user.getOauthId())
                .build();
    }
}
