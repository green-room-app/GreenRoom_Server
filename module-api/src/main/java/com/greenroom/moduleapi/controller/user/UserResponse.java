package com.greenroom.moduleapi.controller.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserResponse {

    @Getter
    @AllArgsConstructor
    public static class JoinResponse {

        private Long id;

        public static JoinResponse from(Long userId) {
            return new JoinResponse(userId);
        }
    }
}
