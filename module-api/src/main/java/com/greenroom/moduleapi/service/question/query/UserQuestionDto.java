package com.greenroom.moduleapi.service.question.query;

import com.greenroom.modulecommon.repository.question.query.QuestionQueryDto;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Builder
public class UserQuestionDto {
    private Long questionId;
    private Long answerId;
    private String question;
    private String category;
    private String answer;
    private String keywords;
    private boolean hasAnswer;
    private boolean hasKeyword;

    public static UserQuestionDto from(QuestionQueryDto queryDto) {
        return UserQuestionDto.builder()
                .questionId(queryDto.getQuestionId())
                .answerId(queryDto.getAnswerId())
                .question(queryDto.getQuestion())
                .category(queryDto.getCategory())
                .answer(queryDto.getAnswer())
                .keywords(queryDto.getKeywords())
                .hasAnswer(StringUtils.isNotEmpty(queryDto.getAnswer()))
                .hasKeyword(StringUtils.isNotEmpty(queryDto.getKeywords()))
                .build();
    }
}
