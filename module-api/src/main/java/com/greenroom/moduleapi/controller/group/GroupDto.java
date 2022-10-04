package com.greenroom.moduleapi.controller.group;

import com.greenroom.modulecommon.entity.group.QuestionGroup;
import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.greenroom.modulecommon.util.KeywordUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class GroupDto {

    @Getter
    public static class CreateRequest {
        @NotNull(message = "categoryId 값은 필수입니다.")
        private Long categoryId;
        @NotEmpty(message = "name 값은 필수입니다.")
        private String name;
    }

    @Getter
    public static class UpdateRequest {
        @NotNull(message = "categoryId 값은 필수입니다.")
        private Long categoryId;
        @NotEmpty(message = "name 값은 필수입니다.")
        private String name;
    }

    @Getter
    public static class MoveRequest {
        @NotNull(message = "groupId 값은 필수입니다.")
        private Long groupId;
        @Size(max = 10)
        private List<Long> ids = new ArrayList<>();
    }

    @Getter
    public static class DeleteRequest {
        @NotNull(message = "groupId 값은 필수입니다.")
        private Long groupId;
        @Size(max = 10)
        private List<Long> ids = new ArrayList<>();
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
        private String name;
        private Long categoryId;
        private Integer questionCnt;

        public static GetResponse from(QuestionGroup group) {
            return GetResponse.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .categoryId(group.getCategory().getId())
                    .questionCnt(group.getInterviewQuestions().size())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GetDetailResponse {
        private Long id;
        private String name;
        private String categoryName;
        private Integer questionCnt;
        private List<GroupQuestionResponse> groupQuestions;

        public static GetDetailResponse of(QuestionGroup group, List<InterviewQuestion> interviewQuestion) {
            return GetDetailResponse.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .categoryName(group.getCategory().getName())
                    .questionCnt(group.getInterviewQuestions().size())
                    .groupQuestions(
                        interviewQuestion.stream()
                            .map(GroupQuestionResponse::from)
                            .collect(toList())
                    )
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GroupQuestionResponse {
        private Long id;
        private String categoryName;
        private boolean isRegister;
        private String question;
        private String answer;
        private List<String> keywords;

        public static GroupQuestionResponse from(InterviewQuestion interviewQuestion) {
            return GroupQuestionResponse.builder()
                    .id(interviewQuestion.getId())
                    .categoryName(interviewQuestion.getCategory().getName())
                    .isRegister(isNotEmpty(interviewQuestion.getKeywords()))
                    .question(interviewQuestion.getQuestion())
                    .answer(interviewQuestion.getAnswer())
                    .keywords(KeywordUtils.toKeywordList(interviewQuestion.getKeywords()))
                    .build();
        }
    }
}
