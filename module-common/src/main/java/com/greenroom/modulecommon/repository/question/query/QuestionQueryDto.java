package com.greenroom.modulecommon.repository.question.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionQueryDto {
    private Long answerId;
    private Long questionId;
    private String question;
    private String category;
    private String answer;
    private String keywords;
}
