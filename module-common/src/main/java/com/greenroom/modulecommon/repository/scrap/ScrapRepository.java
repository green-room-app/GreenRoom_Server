package com.greenroom.modulecommon.repository.scrap;

import com.greenroom.modulecommon.entity.scrap.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapRepository extends JpaRepository<Scrap, Long>, ScrapCustomRepository {
}
