package com.greenroom.modulecommon.repository.interview;

import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long>,
        InterviewQuestionCustomRepository {
}
