package com.greenroom.modulecommon.repository.greenroom.question.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GreenRoomQuestionQueryDto {
    private Long id;    //그린룸질문 id
    private String profileUrl;
    private String name;    //질문자 닉네임
    private int participants;
    private boolean isParticipated;
    private String categoryName;
    private String question;
    private String answer;
    private LocalDateTime expiredAt;
}
