package com.greenroom.modulecommon.repository.greenroom.answer;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GreenRoomQuestionAnswerCustomRepository {
    List<GreenRoomQuestionAnswer> findAll(Long userId, Pageable pageable);

    Optional<GreenRoomQuestionAnswer> find(Long id);

    Optional<GreenRoomQuestionAnswer> find(Long questionId, Long userId);

    boolean exist(Long questionId, Long userId);
}
