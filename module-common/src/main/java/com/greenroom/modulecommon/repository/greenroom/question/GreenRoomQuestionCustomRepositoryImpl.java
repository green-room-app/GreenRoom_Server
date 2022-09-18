package com.greenroom.modulecommon.repository.greenroom.question;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.category.QCategory.category;
import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestion.greenRoomQuestion;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class GreenRoomQuestionCustomRepositoryImpl implements GreenRoomQuestionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 내가 개설한 그린룸 조회
     */
    @Override
    public List<GreenRoomQuestion> findAll(Long userId, Pageable pageable) {
        return jpaQueryFactory
            .selectFrom(greenRoomQuestion)
                .leftJoin(greenRoomQuestion.user, user).fetchJoin()
                .join(greenRoomQuestion.category, category).fetchJoin()
            .where(
                greenRoomQuestion.user.id.eq(userId),
                greenRoomQuestion.isDeleted.eq(false)
            )
            .orderBy(greenRoomQuestion.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
            .fetch();
    }

    @Override
    public Optional<GreenRoomQuestion> find(Long id) {
        GreenRoomQuestion result = jpaQueryFactory
            .selectFrom(greenRoomQuestion)
                .leftJoin(greenRoomQuestion.user, user).fetchJoin()
                .join(greenRoomQuestion.category, category).fetchJoin()
            .where(
                greenRoomQuestion.id.eq(id),
                greenRoomQuestion.isDeleted.eq(false)
            )
            .fetchFirst();

        return Optional.ofNullable(result);
    }
}
