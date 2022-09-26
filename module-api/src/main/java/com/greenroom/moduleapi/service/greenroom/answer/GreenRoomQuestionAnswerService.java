package com.greenroom.moduleapi.service.greenroom.answer;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GreenRoomQuestionAnswerService {
    Long create(Long questionId, Long userId, String answer, List<String> keywordList);

    /**
     * 본인이 참여한 그린룸 질문 목록 조회
     */
    List<GreenRoomQuestionAnswer> getAnswers(List<Long> categoryIds, Long userId, Pageable pageable);

    List<GreenRoomQuestionAnswer> getAnswers(Long questionId, Long userId, Pageable pageable);

    GreenRoomQuestionAnswer getAnswer(Long id);

    GreenRoomQuestionAnswer getAnswer(Long questionId, Long userId);

    boolean isParticipated(Long questionId, Long userId);
}
