package com.greenroom.modulecommon.repository.scrap;

import com.greenroom.modulecommon.entity.scrap.Scrap;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.greenroom.modulecommon.entity.question.QUserQuestion.userQuestion;
import static com.greenroom.modulecommon.entity.scrap.QScrap.scrap;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class ScrapCustomRepositoryImpl implements ScrapCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Scrap> findAll(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(scrap)
                    .join(scrap.user, user).fetchJoin()
                    .join(scrap.userQuestion, userQuestion).fetchJoin()
                .where(
                    scrap.user.id.eq(userId),
                    scrap.used.eq(true)
                )
                .orderBy(scrap.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }
}
