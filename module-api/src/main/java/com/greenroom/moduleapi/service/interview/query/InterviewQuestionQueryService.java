package com.greenroom.moduleapi.service.interview.query;

import com.greenroom.modulecommon.repository.interview.query.InterviewQuestionQueryDto;
import com.greenroom.modulecommon.repository.interview.query.InterviewQuestionQueryRepository;
import com.greenroom.moduleapi.controller.interview.InterviewQuestionSearchOption;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class InterviewQuestionQueryService {

    private final InterviewQuestionQueryRepository questionQueryRepository;

    public List<InterviewQuestionQueryDto> findAll(InterviewQuestionSearchOption searchOption,
                                                   Long userId,
                                                   Pageable pageable) {

        checkArgument(userId != null, "userId 값은 필수입니다.");

        return questionQueryRepository.findAll(userId, searchOption.getCategories(), searchOption.getTitle(), pageable);
    }
}
