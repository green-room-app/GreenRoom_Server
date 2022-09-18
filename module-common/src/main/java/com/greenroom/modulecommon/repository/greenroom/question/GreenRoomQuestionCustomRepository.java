package com.greenroom.modulecommon.repository.greenroom.question;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GreenRoomQuestionCustomRepository {
    List<GreenRoomQuestion> findAll(Long userId, Pageable pageable);

    Optional<GreenRoomQuestion> find(Long id);
}
