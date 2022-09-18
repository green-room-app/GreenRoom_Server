package com.greenroom.modulecommon.repository.greenroom.scrap;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionScrap;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestion.greenRoomQuestion;
import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestionScrap.greenRoomQuestionScrap;
import static com.greenroom.modulecommon.entity.user.QUser.user;


@RequiredArgsConstructor
public class GreenRoomQuestionScrapCustomRepositoryImpl implements GreenRoomQuestionScrapCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<GreenRoomQuestionScrap> findAll(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(greenRoomQuestionScrap)
                    .leftJoin(greenRoomQuestionScrap.user, user).fetchJoin()
                    .join(greenRoomQuestionScrap.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(
                    greenRoomQuestionScrap.user.id.eq(userId),
                    greenRoomQuestionScrap.isDeleted.eq(false)
                )
                .orderBy(greenRoomQuestionScrap.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public Optional<GreenRoomQuestionScrap> find(Long id) {
        GreenRoomQuestionScrap result = jpaQueryFactory
                .selectFrom(greenRoomQuestionScrap)
                    .leftJoin(greenRoomQuestionScrap.user, user).fetchJoin()
                    .join(greenRoomQuestionScrap.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(
                    greenRoomQuestionScrap.id.eq(id),
                    greenRoomQuestionScrap.isDeleted.eq(false))
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<GreenRoomQuestionScrap> find(Long questionId, Long userId) {
        GreenRoomQuestionScrap result = jpaQueryFactory
                .selectFrom(greenRoomQuestionScrap)
                    .leftJoin(greenRoomQuestionScrap.user, user).fetchJoin()
                    .join(greenRoomQuestionScrap.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(
                    greenRoomQuestionScrap.greenRoomQuestion.id.eq(questionId),
                    greenRoomQuestionScrap.user.id.eq(userId),
                    greenRoomQuestionScrap.isDeleted.eq(false)
                )
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean exist(Long questionId, Long userId) {
        GreenRoomQuestionScrap result = jpaQueryFactory
                .selectFrom(greenRoomQuestionScrap)
                    .leftJoin(greenRoomQuestionScrap.user, user).fetchJoin()
                    .join(greenRoomQuestionScrap.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(
                        greenRoomQuestionScrap.greenRoomQuestion.id.eq(questionId),
                        greenRoomQuestionScrap.user.id.eq(userId)
                )
                .fetchFirst();

        return result != null;
    }
}
