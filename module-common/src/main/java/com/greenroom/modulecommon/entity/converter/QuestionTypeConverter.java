package com.greenroom.modulecommon.entity.converter;

import com.greenroom.modulecommon.entity.question.QuestionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class QuestionTypeConverter implements AttributeConverter<QuestionType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(QuestionType questionType) {
        return questionType.getCode();
    }

    @Override
    public QuestionType convertToEntityAttribute(Integer questionTypeCode) {
        return QuestionType.from(questionTypeCode);
    }
}
