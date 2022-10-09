package com.greenroom.modulecommon.repository.interview;

import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InterviewQuestionCustomRepository {
    List<InterviewQuestion> findAllMyQuestions(Long userId, Pageable pageable);

    /**
     * C5-1, C5-2
     * 그룹 안에 들어있는 면접 연습 질문 조회
     */
    Page<InterviewQuestion> findAll(Long groupId, Long userId, Pageable pageable);

    Optional<InterviewQuestion> find(Long id);
//
//    Optional<InterviewQuestion> find(Long questionId, Long userId);
//
//    boolean exist(Long questionId, Long userId);
}
