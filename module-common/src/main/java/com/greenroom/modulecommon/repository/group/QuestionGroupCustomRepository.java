package com.greenroom.modulecommon.repository.group;

import com.greenroom.modulecommon.entity.group.QuestionGroup;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QuestionGroupCustomRepository {
    Optional<QuestionGroup> find(Long id);

    List<QuestionGroup> findAll(Long userId, Pageable pageable);
}
