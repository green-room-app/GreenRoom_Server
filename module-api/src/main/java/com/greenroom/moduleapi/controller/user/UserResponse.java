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

    @Getter
    @AllArgsConstructor
    public static class NameResponse {

        private boolean result;

        public static NameResponse from(boolean result) {
            return new NameResponse(result);
        }
    }
}
