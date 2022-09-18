package com.greenroom.modulecommon.repository.greenroom.answer;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GreenRoomQuestionAnswerRepository extends JpaRepository<GreenRoomQuestionAnswer, Long>,
        GreenRoomQuestionAnswerCustomRepository {
}
