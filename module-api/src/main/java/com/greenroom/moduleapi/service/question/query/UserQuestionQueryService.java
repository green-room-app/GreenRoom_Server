package com.greenroom.moduleapi.service.question.query;

import com.greenroom.modulecommon.repository.question.query.UserQuestionQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserQuestionQueryService {

    private final UserQuestionQueryRepository queryRepository;

    public List<UserQuestionDto> getUserQuestions(Long userId, List<Long> categories, String title, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return queryRepository.findAll(userId, categories, title, pageable)
                .stream()
                .map(UserQuestionDto::from)
                .collect(toList());
    }
}
