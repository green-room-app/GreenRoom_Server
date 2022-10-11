package com.greenroom.moduleapi.controller.interview;

import com.greenroom.modulecommon.entity.group.QuestionGroup;
import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.greenroom.modulecommon.repository.interview.query.InterviewQuestionQueryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class InterviewQuestionDto {

    @Getter
    public static class CreateRequest {
        @NotNull(message = "groupId 값은 필수입니다.")
        private Long groupId;
        @NotNull(message = "questionId 값은 필수입니다.")
        private Long questionId;
        @Min(value = 0)
        @Max(value = 1)
        @NotNull(message = "questionTypeCode 값은 필수입니다.")
        private Integer questionTypeCode;
    }

    @Getter
    public static class UpdateQuestionRequest {
        @NotNull(message = "categoryId 값은 필수입니다.")
        private Long categoryId;
        @NotEmpty(message = "question 값은 필수입니다.")
        private String question;
    }

    @Getter
    public static class UpdateAnswerAndKeywordsRequest {
        private String answer;
        private List<String> keywords;
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
     * C2 화면 면접 질문 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetResponse {
        private Long id;
        private String categoryName;
        private Integer questionTypeCode;
        private String questionType;
        private String question;

        public static GetResponse from(InterviewQuestionQueryDto questionQueryDto) {
            return GetResponse.builder()
                    .id(questionQueryDto.getId())
                    .categoryName(questionQueryDto.getCategoryName())
                    .questionTypeCode(questionQueryDto.getQuestionType().getCode())
                    .questionType(questionQueryDto.getQuestionType().getValue())
                    .question(questionQueryDto.getQuestion())
                    .build();
        }
    }

    /**
     * B11 상세 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetDetailResponse {
        private Long id;
        private String groupName;
        private String groupCategoryName;
        private String categoryName;
        private String question;
        private String answer;
        private List<String> keywords;

        public static GetDetailResponse from(InterviewQuestion interviewQuestion) {
            return GetDetailResponse.builder()
                    .id(interviewQuestion.getId())
                    .groupName(
                            interviewQuestion.getGroup()
                                    .map(QuestionGroup::getName)
                                    .orElse(EMPTY)
                    )
                    .groupCategoryName(
                            interviewQuestion.getGroup()
                                    .map(group -> group.getCategory().getName())
                                    .orElse(EMPTY)
                    )
                    .categoryName(interviewQuestion.getCategory().getName())
                    .question(interviewQuestion.getQuestion())
                    .answer(interviewQuestion.getAnswer())
                    .keywords(interviewQuestion.toKeywordList())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateResponse {
        private Long id;

        public static UpdateResponse from(Long id) {
            return new UpdateResponse(id);
        }
    }
}
