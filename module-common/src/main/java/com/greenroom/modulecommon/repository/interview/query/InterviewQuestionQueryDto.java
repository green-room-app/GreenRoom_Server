package com.greenroom.modulecommon.repository.interview.query;

import com.greenroom.modulecommon.entity.interview.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InterviewQuestionQueryDto {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private QuestionType questionType;
    private String question;
}
