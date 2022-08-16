package com.greenroom.moduleapi.service.scrap;

import com.greenroom.modulecommon.entity.scrap.Scrap;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ScrapService {
    Long create(Long userId, Long userQuestionId);

    List<Scrap> getScraps(Long userId, Pageable pageable);
}
