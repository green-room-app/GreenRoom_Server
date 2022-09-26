package com.greenroom.modulecommon.repository.greenroom.scrap;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GreenRoomQuestionScrapRepository extends JpaRepository<GreenRoomQuestionScrap, Long>,
        GreenRoomQuestionScrapCustomRepository {
}
