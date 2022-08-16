package com.greenroom.modulecommon.repository.question;

import com.greenroom.modulecommon.entity.question.UserQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuestionRepository extends JpaRepository<UserQuestion, Long>, UserQuestionCustomRepository {
}
