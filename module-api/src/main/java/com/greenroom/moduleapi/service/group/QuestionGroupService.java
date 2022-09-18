package com.greenroom.moduleapi.service.group;

import com.greenroom.modulecommon.entity.group.QuestionGroup;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionGroupService {

    Long create(Long categoryId, Long userId, String name);

    List<QuestionGroup> getGroups(Long userId, Pageable pageable);

    QuestionGroup getGroup(Long id);

    Long update(Long id, Long categoryId, String name);

    Long update(Long id, Long categoryId);

    Long update(Long id, String name);

    boolean isOwner(Long id, Long userId);

    void delete(Long id);
}
