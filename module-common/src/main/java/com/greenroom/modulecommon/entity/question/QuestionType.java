package com.greenroom.modulecommon.entity.question;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Getter
@AllArgsConstructor
public enum QuestionType {

    PUBLIC(0, "지식in 질문"),
    PRIVATE(1, "일반 질문(나만 답변)");

    private static final Map<Integer, QuestionType> questionTypeMap =
            Stream.of(values()).collect(toMap(QuestionType::getCode, value -> value));

    private Integer code;
    private String value;

    public static QuestionType from(int questionTypeCode) {
        QuestionType questionType = questionTypeMap.get(questionTypeCode);
        if (questionType == null) {
            throw new IllegalArgumentException("잘못된 QuestionType 타입입니다. 0,1 중 하나를 입력해야 합니다.");
        }

        return questionType;
    }
}
