package com.greenroom.moduleapi.service.greenroom.scrap;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionScrap;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GreenRoomQuestionScrapService {
    Long create(Long questionId, Long userId);

    /**
     * 본인이 스크랩 목록 조회
     */
    List<GreenRoomQuestionScrap> getScraps(Long userId, Pageable pageable);

    GreenRoomQuestionScrap getScrap(Long id);

    GreenRoomQuestionScrap getScrap(Long questionId, Long userId);

    void delete(Long id);
}
