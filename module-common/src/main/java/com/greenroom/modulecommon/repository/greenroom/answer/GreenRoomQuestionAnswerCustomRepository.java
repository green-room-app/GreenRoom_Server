package com.greenroom.modulecommon.repository.greenroom.answer;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GreenRoomQuestionAnswerCustomRepository {
    List<GreenRoomQuestionAnswer> findAll(List<Long> categoryIds, Long userId, Pageable pageable);
    /**
     * 특정 질문에 참여한 그린룸 답변 출력, 단 userId가 작성한 답변이 최상단
     */
    List<GreenRoomQuestionAnswer> findAll(Long questionId, Long userId, Pageable pageable);

    Optional<GreenRoomQuestionAnswer> find(Long id);

    Optional<GreenRoomQuestionAnswer> find(Long questionId, Long userId);
    /**
     * 그린룸질문 참여여부 판단
     */
    boolean exist(Long questionId, Long userId);
}
