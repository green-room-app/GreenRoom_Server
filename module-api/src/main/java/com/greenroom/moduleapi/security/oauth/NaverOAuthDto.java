package com.greenroom.moduleapi.security.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

public class NaverOAuthDto {

    @Getter
    @AllArgsConstructor
    public static class LoginRequest {
        private String accessToken;

        public static NaverOAuthDto.LoginRequest from(String accessToken) {
            return new NaverOAuthDto.LoginRequest(accessToken);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class LogoutRequest {
        private String accessToken;

        public static NaverOAuthDto.LogoutRequest from(String accessToken) {
            return new NaverOAuthDto.LogoutRequest(accessToken);
        }
    }

    /**
     * OAuthService의 사용자 정보 응답으로부터 사용하고자 하는 값
     *
     * json response
     *
     * {
     *     "resultcode": "00",
     *     "message": "success",
     *     "response": {
     *         "id": "la_etLzQxEW1FYS9ipZ4TwNurvVJCyNhaUvtNp3z5P4"
     *     }
     * }
     */
    @Getter
    public static class LoginResponse {
        private String id;

        @SuppressWarnings("unchecked")
        @JsonProperty("response")
        private void getOauthId(Map<String, Object> naverAccountInfo) {
            this.id = (String) naverAccountInfo.get("id");
        }
    }

    @Getter
    public static class LogoutResponse {
        private Long id;
    }
}
