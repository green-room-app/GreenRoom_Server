package com.greenroom.modulecommon.repository.greenroom.scrap;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionScrap;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GreenRoomQuestionScrapCustomRepository {
    List<GreenRoomQuestionScrap> findAll(Long userId, Pageable pageable);

    Optional<GreenRoomQuestionScrap> find(Long id);

    Optional<GreenRoomQuestionScrap> find(Long questionId, Long userId);
    /**
     * 그린룸질문 스크랩 여부 판단
     */
    boolean exist(Long questionId, Long userId);
}
