package com.greenroom.moduleapi.service.greenroom.question;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface GreenRoomQuestionService {
    Long create(Long userId, String question, Long categoryId, LocalDateTime expiredAt);

    List<GreenRoomQuestion> getGreenRoomQuestions(Long userId, Pageable pageable);

    GreenRoomQuestion getGreenRoomQuestion(Long id);

    void delete(Long id);
}
