package com.greenroom.moduleapi.service.greenroom.question.query;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GreenRoomQuestionDetailDto {
    private Long id;    //그린룸 질문 id
    private boolean isScrap;
    private boolean isParticipated;
    private boolean isWriter;
    private boolean isExpired;
    private int participants;
    private LocalDateTime expiredAt;
    private String question;
    private String categoryName;
}
