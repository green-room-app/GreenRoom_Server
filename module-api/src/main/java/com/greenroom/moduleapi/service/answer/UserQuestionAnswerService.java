package com.greenroom.moduleapi.service.answer;

import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import com.greenroom.modulecommon.entity.question.QuestionType;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserQuestionAnswerService {
    Long createPrivateQuestion(Long userId, String question, Long categoryId);

    Long createPrivateQuestionAnswer(Long userQuestionId, Long userId, String answer);

    List<UserQuestionAnswer> getUserQuestionAnswers(Long userId, QuestionType questionType, Pageable pageable);

    UserQuestionAnswer getUserQuestionAnswer(Long userQuestionId, Long userId);

    UserQuestionAnswer getUserQuestionAnswer(Long id);

    boolean isWriter(Long id, Long userId);

    void update(Long id, String answer);
}
