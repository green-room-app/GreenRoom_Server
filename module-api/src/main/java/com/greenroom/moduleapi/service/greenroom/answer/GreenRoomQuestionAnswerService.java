package com.greenroom.moduleapi.service.greenroom.answer;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GreenRoomQuestionAnswerService {
    Long create(Long questionId, String answer, Long userId);

    /**
     * 본인이 참여한 그린룸 질문 목록 조회
     */
    List<GreenRoomQuestionAnswer> getAnswers(Long userId, Pageable pageable);

    GreenRoomQuestionAnswer getAnswer(Long id);

    GreenRoomQuestionAnswer getAnswer(Long questionId, Long userId);

    boolean exist(Long questionId, Long userId);
}
