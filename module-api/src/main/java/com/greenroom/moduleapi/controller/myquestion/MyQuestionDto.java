package com.greenroom.moduleapi.controller.myquestion;

import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.greenroom.modulecommon.repository.interview.query.MyQuestionQueryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class MyQuestionDto {

    @Getter
    public static class CreateRequest {
        @NotNull(message = "categoryId 값은 필수입니다.")
        private Long categoryId;
        @NotEmpty(message = "question 값은 필수입니다.")
        private String question;
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

    /**
     * B10 마이 질문리스트 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetResponse {
        private Long id;
        private String groupName;
        private String groupCategoryName;
        private String categoryName;
        private String question;

        public static GetResponse from(MyQuestionQueryDto questionQueryDto) {
            return GetResponse.builder()
                    .id(questionQueryDto.getId())
                    .groupName(questionQueryDto.getGroupName())
                    .groupCategoryName(questionQueryDto.getGroupCategoryName())
                    .categoryName(questionQueryDto.getQuestionCategoryName())
                    .build();
        }
    }

    /**
     * B11 마이 질문리스트 상세 조회 결과 DTO
     */
    @Getter
    @Builder
    public static class GetDetailResponse {
        private Long id;
        private String groupCategoryName;
        private String categoryName;
        private String question;
        private String answer;
        private List<String> keywords;

        public static GetDetailResponse from(InterviewQuestion interviewQuestion) {
            return GetDetailResponse.builder()
                    .id(interviewQuestion.getId())
                    .groupCategoryName(
                        interviewQuestion.getGroup()
                            .map(group -> group.getCategory().getName())
                            .orElse(StringUtils.EMPTY)
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
    public static class CreateResponse {
        private Long id;

        public static CreateResponse from(Long id) {
            return new CreateResponse(id);
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
