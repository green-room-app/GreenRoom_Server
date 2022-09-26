package com.greenroom.modulecommon.repository.greenroom.question.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RecentQuestionQueryDto {
    private Long id;    //그린룸질문 id
    private String profileUrl;
    private boolean isParticipated;
    private String categoryName;
    private String question;
    private LocalDateTime expiredAt;
}
