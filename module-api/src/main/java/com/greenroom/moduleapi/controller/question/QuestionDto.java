package com.greenroom.moduleapi.controller.question;

import com.greenroom.moduleapi.service.answer.query.RefQuestionAnswerDto;
import com.greenroom.moduleapi.service.question.query.RefQuestionDto;
import com.greenroom.moduleapi.service.question.query.UserQuestionDto;
import com.greenroom.modulecommon.entity.answer.RefQuestionAnswer;
import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class QuestionDto {

    public static final String REFERENCE = "basic";
    public static final String USER = "greenroom";

    @Getter
    @Builder
    public static class GetResponse {
        private Long questionId;
        private Long answerId;
        private String category;
        private String questionType;
        private String question;
        private boolean hasAnswer;
        private boolean hasKeyword;

        public static GetResponse from(RefQuestionDto refQuestionDto) {
            return GetResponse.builder()
                    .questionId(refQuestionDto.getQuestionId())
                    .answerId(refQuestionDto.getAnswerId())
                    .category(refQuestionDto.getCategory())
                    .questionType(REFERENCE)
                    .question(refQuestionDto.getQuestion())
                    .hasAnswer(refQuestionDto.isHasAnswer())
                    .hasKeyword(refQuestionDto.isHasKeyword())
                    .build();
        }

        public static GetResponse from(UserQuestionDto userQuestionDto) {
            return GetResponse.builder()
                    .questionId(userQuestionDto.getQuestionId())
                    .answerId(userQuestionDto.getAnswerId())
                    .category(userQuestionDto.getCategory())
                    .questionType(USER)
                    .question(userQuestionDto.getQuestion())
                    .hasAnswer(userQuestionDto.isHasAnswer())
                    .hasKeyword(userQuestionDto.isHasKeyword())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetDetailResponse {
        private Long questionId;
        private String question;
        private String answer;
        private List<String> keywords;

        public static GetDetailResponse from(RefQuestionAnswerDto refQuestionAnswer) {
            return null;
        }

        public static GetDetailResponse from(UserQuestionAnswer userQuestionAnswer) {
            return null;
        }
    }

}
