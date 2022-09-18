package com.greenroom.modulecommon.repository.greenroom.question;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GreenRoomQuestionRepository extends JpaRepository<GreenRoomQuestion, Long>, GreenRoomQuestionCustomRepository {
}
