package com.greenroom.modulecommon.repository.answer;

import com.greenroom.modulecommon.entity.answer.RefQuestionAnswer;

import java.util.Optional;

public interface RefQuestionAnswerCustomRepository {
    Optional<RefQuestionAnswer> find(Long refQuestionId, Long userId);
}
