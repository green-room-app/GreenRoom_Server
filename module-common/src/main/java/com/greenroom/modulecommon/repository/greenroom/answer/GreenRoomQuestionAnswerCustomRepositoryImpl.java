package com.greenroom.modulecommon.repository.greenroom.answer;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestion.greenRoomQuestion;
import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestionAnswer.greenRoomQuestionAnswer;
import static com.greenroom.modulecommon.entity.user.QUser.user;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class GreenRoomQuestionAnswerCustomRepositoryImpl implements GreenRoomQuestionAnswerCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<GreenRoomQuestionAnswer> findAll(List<Long> categoryIds, Long userId, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(greenRoomQuestionAnswer)
                    .leftJoin(greenRoomQuestionAnswer.user, user).fetchJoin()
                    .join(greenRoomQuestionAnswer.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(
                    greenRoomQuestionAnswer.user.id.eq(userId),
                    categoriesEq(categoryIds)
                )
                .orderBy(greenRoomQuestionAnswer.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public List<GreenRoomQuestionAnswer> findAll(Long questionId, Long userId, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(greenRoomQuestionAnswer)
                .leftJoin(greenRoomQuestionAnswer.user, user).fetchJoin()
                .join(greenRoomQuestionAnswer.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(
                    greenRoomQuestionAnswer.greenRoomQuestion.id.eq(questionId)
                )
                .orderBy(greenRoomQuestionAnswer.user.id.subtract(userId).abs().asc().nullsLast())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public Optional<GreenRoomQuestionAnswer> find(Long id) {
        GreenRoomQuestionAnswer result = jpaQueryFactory
                .selectFrom(greenRoomQuestionAnswer)
                    .leftJoin(greenRoomQuestionAnswer.user, user).fetchJoin()
                    .join(greenRoomQuestionAnswer.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(greenRoomQuestionAnswer.id.eq(id))
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<GreenRoomQuestionAnswer> find(Long questionId, Long userId) {
        GreenRoomQuestionAnswer result = jpaQueryFactory
                .selectFrom(greenRoomQuestionAnswer)
                    .leftJoin(greenRoomQuestionAnswer.user, user).fetchJoin()
                    .join(greenRoomQuestionAnswer.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(
                    greenRoomQuestionAnswer.greenRoomQuestion.id.eq(questionId),
                    greenRoomQuestionAnswer.user.id.eq(userId)
                )
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public boolean exist(Long questionId, Long userId) {
        GreenRoomQuestionAnswer result = jpaQueryFactory
                .selectFrom(greenRoomQuestionAnswer)
                    .leftJoin(greenRoomQuestionAnswer.user, user).fetchJoin()
                    .join(greenRoomQuestionAnswer.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(
                    greenRoomQuestionAnswer.greenRoomQuestion.id.eq(questionId),
                    greenRoomQuestionAnswer.user.id.eq(userId)
                )
                .fetchFirst();

        return result != null;
    }

    private BooleanExpression categoriesEq(List<Long> categoryIds) {
        if (isEmpty(categoryIds)) {
            return null;
        }

        return greenRoomQuestionAnswer.greenRoomQuestion.category.id.in(categoryIds);
    }
}
