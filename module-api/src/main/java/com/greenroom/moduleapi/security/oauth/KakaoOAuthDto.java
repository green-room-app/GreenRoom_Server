package com.greenroom.moduleapi.security.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class KakaoOAuthDto {

    @Getter
    @AllArgsConstructor
    public static class LoginRequest {
        private String accessToken;

        public static KakaoOAuthDto.LoginRequest from(String accessToken) {
            return new KakaoOAuthDto.LoginRequest(accessToken);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class LogoutRequest {
        private String accessToken;

        public static KakaoOAuthDto.LogoutRequest from(String accessToken) {
            return new KakaoOAuthDto.LogoutRequest(accessToken);
        }
    }

    /**
     * OAuthService의 사용자 정보 응답으로부터 사용하고자 하는 값
     *
     * json response
     *
     * {
     *    "id": 1447910803,
     *    "connected_at": "2020-08-11T16:01:56Z"
     * }
     */
    @Getter
    public static class LoginResponse {
        private String id;
    }

    @Getter
    public static class LogoutResponse {
        private String id;
    }
}
