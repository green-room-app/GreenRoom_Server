package com.greenroom.modulecommon.repository.answer;

import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import com.greenroom.modulecommon.entity.question.QuestionType;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserQuestionAnswerCustomRepository {
    List<UserQuestionAnswer> findAllUserQuestionAnswer(Long userId, QuestionType questionType, Pageable pageable);
    Optional<UserQuestionAnswer> find(Long userQuestionId, Long userId);
    Optional<UserQuestionAnswer> find(Long id);
}
