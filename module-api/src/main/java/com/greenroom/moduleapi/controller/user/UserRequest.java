package com.greenroom.moduleapi.controller.user;

import lombok.Getter;

import javax.validation.constraints.*;

public class UserRequest {

    @Getter
    public static class JoinRequest {
        @NotEmpty(message = "accessToken 값은 필수입니다.")
        private String accessToken;

        @Min(value = 0)
        @Max(value = 2)
        @NotNull(message = "oauthType 값은 필수입니다.")
        private Integer oauthType;
    }

}
