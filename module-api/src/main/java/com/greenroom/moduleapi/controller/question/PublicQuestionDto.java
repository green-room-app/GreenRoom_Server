package com.greenroom.moduleapi.controller.question;

import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.entity.scrap.Scrap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

public class PublicQuestionDto {

    @Getter
    public static class CreateRequest {
        @NotEmpty(message = "question 값은 필수입니다.")
        private String question;

        @NotNull(message = "categoryId 값은 필수입니다.")
        private Long categoryId;

        @NotNull(message = "expiredAt 값은 필수입니다.")
        private LocalDateTime expiredAt;
    }

    @Getter
    public static class ScrapRequest {
        @NotNull(message = "questionId 값은 필수입니다.")
        private Long questionId;
    }


    @Getter
    @AllArgsConstructor
    public static class CreateResponse {
        private Long id;

        public static CreateResponse from(Long id) {
            return new CreateResponse(id);
        }
    }

    /**
     * 최신 그린룸질문(지식in 타입) 응답값
     */
    @Getter
    @Builder
    public static class RecentQuestionResponse {
        private Long id;
        private String profileImage;
        private String category;
        private String question;

        public static RecentQuestionResponse of(UserQuestion userQuestion, String profileImageUrl) {
            return RecentQuestionResponse.builder()
                    .id(userQuestion.getId())
                    .profileImage(profileImageUrl)
                    .category(userQuestion.getCategory().getName())
                    .question(userQuestion.getQuestion())
                    .build();
        }
    }

    /**
     * 자신이 참여한 그린룸질문(지식in 타입) 응답값
     */
    @Getter
    @Builder
    public static class InvolveQuestionResponse {
        private Long id;
        private String profileImage;
        private String category;
        private String question;

        public static InvolveQuestionResponse of(UserQuestionAnswer userQuestionAnswer, String profileImageUrl) {
            return InvolveQuestionResponse.builder()
                    .id(userQuestionAnswer.getUserQuestion().getId())
                    .profileImage(profileImageUrl)
                    .category(userQuestionAnswer.getUserQuestion().getCategory().getName())
                    .question(userQuestionAnswer.getUserQuestion().getQuestion())
                    .build();
        }
    }

    /**
     * 자신이 스크랩한 그린룸질문(지식in 타입) 응답값
     * (관심있는 질문)
     */
    @Getter
    @Builder
    public static class ScrapQuestionResponse {
        private Long id;
        private String profileImage;
        private String category;
        private String question;
        private Long remainedTime;

        public static ScrapQuestionResponse of(Scrap scrap, String profileImageUrl) {
            return ScrapQuestionResponse.builder()
                    .id(scrap.getUserQuestion().getId())
                    .profileImage(profileImageUrl)
                    .category(scrap.getUserQuestion().getCategory().getName())
                    .question(scrap.getUserQuestion().getQuestion())
                    .remainedTime(
                        Duration.between(LocalDateTime.now(), scrap.getUserQuestion().getExpiredAt()).getSeconds()
                    )
                    .build();
        }
    }

    /**
     * 인기 그린룸질문(지식in 타입) 응답값
     */
    @Getter
    @Builder
    public static class PopularQuestionResponse {
        private Long id;
        private String profileImage;
        private String category;
        private String question;
        private int participants;
        private Long remainedTime;

        public static PopularQuestionResponse of(UserQuestion userQuestion, String profileImageUrl) {
            return PopularQuestionResponse.builder()
                    .id(userQuestion.getId())
                    .profileImage(profileImageUrl)
                    .category(userQuestion.getCategory().getName())
                    .question(userQuestion.getQuestion())
                    .participants(userQuestion.getParticipants())
                    .remainedTime(Duration.between(LocalDateTime.now(), userQuestion.getExpiredAt()).getSeconds())
                    .build();
        }
    }
}
