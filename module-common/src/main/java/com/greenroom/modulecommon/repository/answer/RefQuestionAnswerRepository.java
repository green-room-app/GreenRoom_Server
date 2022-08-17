package com.greenroom.modulecommon.repository.answer;

import com.greenroom.modulecommon.entity.answer.RefQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefQuestionAnswerRepository extends JpaRepository<RefQuestionAnswer, Long>,
        RefQuestionAnswerCustomRepository {
}
