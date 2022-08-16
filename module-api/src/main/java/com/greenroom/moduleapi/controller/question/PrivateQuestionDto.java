package com.greenroom.moduleapi.controller.question;

import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class PrivateQuestionDto {

    @Getter
    @AllArgsConstructor
    public static class CreateQuestionRequest {
        @NotNull(message = "categoryId 값은 필수입니다.")
        private Long categoryId;

        @NotEmpty(message = "question 값은 필수입니다.")
        private String question;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateQuestionRequest {
        @NotNull(message = "categoryId 값은 필수입니다.")
        private Long categoryId;

        @NotEmpty(message = "question 값은 필수입니다.")
        private String question;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateAnswerRequest {
        @NotNull(message = "questionId 값은 필수입니다.")
        private Long questionId;

        @NotEmpty(message = "answer 값은 필수입니다.")
        private String answer;
    }

    @Getter
    public static class UpdateAnswerRequest {
        @NotEmpty(message = "answer 값은 필수입니다.")
        private String answer;
    }

    @Getter
    @AllArgsConstructor
    public static class CreateResponse {
        private Long id;

        public static CreateResponse from(Long id) {
            return new CreateResponse(id);
        }
    }

    @Getter
    @Builder
    public static class GetResponse {
        private Long id;
        private String profileImage;
        private String category;
        private String question;

        public static GetResponse of(UserQuestion userQuestion, String profileImageUrl) {
            return GetResponse.builder()
                    .id(userQuestion.getId())
                    .profileImage(profileImageUrl)
                    .category(userQuestion.getCategory().getName())
                    .question(userQuestion.getQuestion())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetDetailResponse {
        private Long questionId;
        private String profileImage;
        private String category;
        private String question;
        private Long answerId;
        private String answer;

        public static GetDetailResponse of(UserQuestionAnswer userQuestionAnswer, String profileImageUrl) {
            return GetDetailResponse.builder()
                    .questionId(userQuestionAnswer.getUserQuestion().getId())
                    .profileImage(profileImageUrl)
                    .category(userQuestionAnswer.getUserQuestion().getCategory().getName())
                    .question(userQuestionAnswer.getUserQuestion().getQuestion())
                    .answerId(userQuestionAnswer.getId())
                    .answer(userQuestionAnswer.getAnswer())
                    .build();
        }
    }

}
