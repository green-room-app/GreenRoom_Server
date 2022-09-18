package com.greenroom.modulecommon.repository.group;

import com.greenroom.modulecommon.entity.group.QuestionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long>, QuestionGroupCustomRepository {
}
