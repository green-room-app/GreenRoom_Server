package com.greenroom.moduleapi.controller.auth;

import lombok.AllArgsConstructor;
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
    public static class ReissueRequest {
        @NotEmpty(message = "accessToken 값은 필수입니다.")
        private String accessToken;

        @NotEmpty(message = "refreshToken 을 입력해주세요.")
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    public static class AuthResponse {
        //jwt
        private String apiToken;

        //refreshToken
        private String refreshToken;

        public static AuthResponse of(String apiToken, String refreshToken) {
            return new AuthResponse(apiToken, refreshToken);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ReissueResponse {
        //jwt
        private String apiToken;

        //refreshToken
        private String refreshToken;

        public static ReissueResponse of(String apiToken, String refreshToken) {
            return new ReissueResponse(apiToken, refreshToken);
        }
    }
}
