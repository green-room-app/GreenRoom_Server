package com.greenroom.moduleapi.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private final RedisTemplate<String, String> redisTokenTemplate;

    @Transactional
    public void saveRefreshToken(Long userId, String refreshToken) {
        redisTokenTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + userId, refreshToken, Duration.ofDays(1));
    }

    //FIXME: RuntimeException 제거
    public String getRefreshToken(Long userId) {
        String refreshToken = redisTokenTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);

        if (refreshToken == null) {
            throw new RuntimeException();
        }

        return refreshToken;
    }
}
