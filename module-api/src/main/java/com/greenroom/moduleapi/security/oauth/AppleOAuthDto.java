package com.greenroom.moduleapi.security.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class AppleOAuthDto {

    @Getter
    @AllArgsConstructor
    public static class LoginRequest {
        private String identityToken;

        public static AppleOAuthDto.LoginRequest from(String identityToken) {
            return new AppleOAuthDto.LoginRequest(identityToken);
        }
    }

    @Getter
    public static class AuthKeyResponse {
        private List<PublicKeyResponse> keys;
    }

    @Getter
    public static class PublicKeyResponse {
        private String kty;
        private String kid;
        private String use;
        private String alg;
        private String n;
        private String e;
    }

    @Getter
    @AllArgsConstructor
    public static class LoginResponse {
        private String id;

        public static LoginResponse from(String userId) {
            return new LoginResponse(userId);
        }
    }

}
