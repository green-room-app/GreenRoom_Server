package com.greenroom.modulecommon.repository.answer;

import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuestionAnswerRepository extends JpaRepository<UserQuestionAnswer, Long>,
        UserQuestionAnswerCustomRepository {


}
