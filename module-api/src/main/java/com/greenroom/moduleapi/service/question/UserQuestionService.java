package com.greenroom.moduleapi.service.question;

import com.greenroom.modulecommon.entity.question.QuestionType;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface UserQuestionService {
    /**
     * 그린룸 '방 개설하기'
     */
    Long createPublicQuestion(Long userId, String question, Long categoryId, LocalDateTime expiredAt);

    /**
     * 그린룸 '질문 만들기'
     */
    Long createPrivateQuestion(Long userId, String question, Long categoryId);

    UserQuestion getUserQuestion(Long id);

    List<UserQuestion> getUserQuestions(List<Long> categories, QuestionType questionType, Pageable pageable);

    List<UserQuestion> getPopularQuestions(Pageable pageable);

    boolean isWriter(Long id, Long userId);

    void update(Long id, String question, Long categoryId);

    void deletePrivateQuestion(Long id);
}
