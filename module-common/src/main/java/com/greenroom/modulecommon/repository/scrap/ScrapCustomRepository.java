package com.greenroom.modulecommon.repository.scrap;

import com.greenroom.modulecommon.entity.scrap.Scrap;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ScrapCustomRepository {
    List<Scrap> findAll(Long userId, Pageable pageable);
}
