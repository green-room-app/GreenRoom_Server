package com.greenroom.modulecommon.entity.interview;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Getter
@AllArgsConstructor
public enum QuestionType {

    BASIC_QUESTION(0, "기본질문"),
    MY_QUESTION(1, "마이질문"),
    BASIC_QUESTION_WITH_GROUP(2, "그룹에 담은 기본질문"),
    MY_QUESTION_WITH_GROUP(3, "그룹에 담은 마이질문"),
    GREENROOM_QUESTION(4, "그린룸질문");

    private static final Map<Integer, QuestionType> questionTypeMap =
            Stream.of(values()).collect(toMap(QuestionType::getCode, value -> value));

    private Integer code;
    private String value;

    public static QuestionType from(int questionTypeCode) {
        QuestionType questionType = questionTypeMap.get(questionTypeCode);
        if (questionType == null) {
            throw new IllegalArgumentException("잘못된 QuestionType 타입입니다. 0-4 중 하나를 입력해야 합니다.");
        }

        return questionType;
    }
}
