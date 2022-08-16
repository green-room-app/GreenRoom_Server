package com.greenroom.modulecommon.repository.question;

import com.greenroom.modulecommon.entity.question.QuestionType;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserQuestionCustomRepository {
    List<UserQuestion> findAll(List<Long> categories, QuestionType questionType, Pageable pageable);

    List<UserQuestion> findAll(Long userId, QuestionType questionType, Pageable pageable);

    Optional<UserQuestion> find(Long id);

    /**
     * FOR 인기질문 API
     */
    List<UserQuestion> findPopularQuestions(Pageable pageable);
}
