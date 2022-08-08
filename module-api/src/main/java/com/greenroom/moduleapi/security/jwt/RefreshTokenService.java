package com.greenroom.moduleapi.security.jwt;

import com.greenroom.modulecommon.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private final RedisTemplate<String, String> redisTokenTemplate;

    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken) {
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(isNotEmpty(refreshToken), "refreshToken 값은 필수입니다.");

        redisTokenTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + userId, refreshToken, Duration.ofDays(1));
    }

    @Transactional
    public void deleteRefreshToken(Long userId) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        redisTokenTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    public String getRefreshToken(Long userId) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        String refreshToken = redisTokenTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);

        if (refreshToken == null) {
            throw new ApiException(NOT_FOUND, String.format("userId = %s", userId));
        }

        return refreshToken;
    }
}
