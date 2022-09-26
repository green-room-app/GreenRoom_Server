package com.greenroom.moduleapi.service.greenroom.answer.query;

import com.greenroom.modulecommon.repository.greenroom.answer.query.GreenRoomQuestionAnswerQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GreenRoomQuestionAnswerQueryService {

    private final GreenRoomQuestionAnswerQueryRepository questionRepository;

    public List<String> getUserProfileImages(Long questionId, int size) {
        checkArgument(questionId != null, "questionId 값은 필수입니다.");
        int pageSize = Math.min(3, size);
        return questionRepository.getUserProfileImages(questionId, pageSize);
    }
}
