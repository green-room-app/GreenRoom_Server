package com.greenroom.modulecommon.repository.greenroom.question;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface GreenRoomQuestionCustomRepository {
    /**
     * 내가 개설한 그린룸 조회
     */
    Slice<GreenRoomQuestion> findMyGreenRoomQuestions(Long userId, Pageable pageable);

    Optional<GreenRoomQuestion> find(Long id);
}
