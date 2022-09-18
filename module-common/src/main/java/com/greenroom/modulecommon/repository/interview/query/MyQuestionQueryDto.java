package com.greenroom.modulecommon.repository.interview.query;

import com.greenroom.modulecommon.entity.interview.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * B10 화면 마이 질문리스트 조회용 쿼리
 *
 private Long id;
 private String groupCategoryName;
 private String groupName;
 private String questionCategoryName;
 private QuestionType questionType;
 private String question;
 */
@Getter
@Builder
@AllArgsConstructor
public class MyQuestionQueryDto {
    private Long id;
    private String groupCategoryName;
    private String groupName;
    private String questionCategoryName;
    private QuestionType questionType;
    private String question;
}
