package com.greenroom.moduleapi.controller.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class AuthDto {

    @Getter
    public static class LoginRequest {
        @NotEmpty(message = "accessToken 값은 필수입니다.")
        private String accessToken;

        @Min(value = 0)
        @Max(value = 2)
        @NotNull(message = "oauthType 값은 필수입니다.")
        private Integer oauthType;
    }

    @Getter
    public static class LogoutRequest {
        @NotEmpty(message = "accessToken 값은 필수입니다.")
        private String accessToken;

        @Min(value = 0)
        @Max(value = 2)
        @NotNull(message = "oauthType 값은 필수입니다.")
        private Integer oauthType;
    }

    @Getter
    public static class ReissueRequest {
        @NotEmpty(message = "accessToken 값은 필수입니다.")
        private String accessToken;

        @NotEmpty(message = "refreshToken 을 입력해주세요.")
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AuthResponse {
        //jwt
        private String accessToken;

        //refreshToken
        private String refreshToken;

        //accessToken 유효 시간
        private int expiresIn;

        public static AuthResponse of(String accessToken, String refreshToken, int expiresIn) {
            return new AuthResponse(accessToken, refreshToken, expiresIn);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ReissueResponse {
        //jwt
        private String accessToken;

        //refreshToken
        private String refreshToken;

        //accessToken 유효 시간
        private int expiresIn;

        public static ReissueResponse of(String accessToken, String refreshToken, int expiresIn) {
            return new ReissueResponse(accessToken, refreshToken, expiresIn);
        }
    }
}
