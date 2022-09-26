package com.greenroom.moduleapi.service.greenroom.question;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface GreenRoomQuestionService {
    Long create(Long userId, String question, Long categoryId, LocalDateTime expiredAt);

    Slice<GreenRoomQuestion> getMyGreenRoomQuestions(Long userId, Pageable pageable);

    GreenRoomQuestion getGreenRoomQuestion(Long id);

    boolean isOwner(Long id, Long userId);

    void delete(Long id);
}
