package com.greenroom.moduleapi.service.answer.query;

import com.greenroom.modulecommon.entity.answer.RefQuestionAnswer;
import com.greenroom.modulecommon.entity.question.RefQuestion;

import java.util.List;

public class RefQuestionAnswerDto {
    private Long questionId;
    private String question;
    private String answer;
    private List<String> keywords;

    public static RefQuestionAnswerDto from(RefQuestionAnswer refQuestionAnswer) {
        return null;
    }

    public static RefQuestionAnswerDto from(RefQuestion refQuestion) {
        return null;
    }
}
