package com.greenroom.moduleapi.service.answer.query;

import com.greenroom.modulecommon.entity.question.RefQuestion;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.answer.RefQuestionAnswerRepository;
import com.greenroom.modulecommon.repository.question.RefQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefQuestionAnswerQueryService {

    private final RefQuestionAnswerRepository refQuestionAnswerRepository;
    private final RefQuestionRepository refQuestionRepository;

    public RefQuestionAnswerDto getRefQuestionAnswer(Long refQuestionId, Long userId) {
        checkArgument(refQuestionId != null, "refQuestionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        RefQuestionAnswerDto result = refQuestionAnswerRepository.find(refQuestionId, userId)
                .map(RefQuestionAnswerDto::from)
                .orElse(null);

        if (result != null) {
            return result;
        }

        return refQuestionRepository.findById(refQuestionId).map(RefQuestionAnswerDto::from)
            .orElseThrow(() -> new ApiException(NOT_FOUND, RefQuestion.class,
                String.format("id = %s", refQuestionId)));
    }
}
