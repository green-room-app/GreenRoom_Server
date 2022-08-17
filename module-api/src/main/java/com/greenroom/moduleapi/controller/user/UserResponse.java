package com.greenroom.moduleapi.controller.user;

import com.greenroom.modulecommon.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static org.apache.commons.lang3.StringUtils.EMPTY;

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
    @Builder
    public static class GetResponse {
        private Long categoryId;
        private String category;
        private String name;
        private String profileImage;

        public static GetResponse from(User user) {
            return GetResponse.builder()
                    .categoryId(user.getCategory().getId())
                    .category(user.getCategory().getName())
                    .name(user.getName())
                    .profileImage(EMPTY)
                    .build();
        }

        public static GetResponse of(User user, String profileUrl) {
            return GetResponse.builder()
                    .categoryId(user.getCategory().getId())
                    .category(user.getCategory().getName())
                    .name(user.getName())
                    .profileImage(profileUrl)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateProfileImageResponse {
        private String profileImage;

        public static UpdateProfileImageResponse from(String profileUrl) {
            return new UpdateProfileImageResponse(profileUrl);
        }
    }
}
