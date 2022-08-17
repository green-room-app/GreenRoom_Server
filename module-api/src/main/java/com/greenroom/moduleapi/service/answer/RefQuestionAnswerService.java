package com.greenroom.moduleapi.service.answer;

import com.greenroom.modulecommon.entity.answer.RefQuestionAnswer;

public interface RefQuestionAnswerService {
    Long create(Long refQuestionId, Long userId, String answer);
}
