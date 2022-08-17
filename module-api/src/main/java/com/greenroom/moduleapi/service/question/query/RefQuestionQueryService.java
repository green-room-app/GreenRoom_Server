package com.greenroom.moduleapi.service.question.query;

import com.greenroom.modulecommon.repository.question.query.RefQuestionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RefQuestionQueryService {

    private final RefQuestionQueryRepository queryRepository;

    public List<RefQuestionDto> getRefQuestions(Long userId, List<Long> categories, String title, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return queryRepository.findAll(userId, categories, title, pageable)
                .stream()
                .map(RefQuestionDto::from)
                .collect(toList());
    }
}
