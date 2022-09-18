package com.greenroom.modulecommon.repository.greenroom.answer;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestion.greenRoomQuestion;
import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestionAnswer.greenRoomQuestionAnswer;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class GreenRoomQuestionAnswerCustomRepositoryImpl implements GreenRoomQuestionAnswerCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<GreenRoomQuestionAnswer> findAll(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(greenRoomQuestionAnswer)
                    .leftJoin(greenRoomQuestionAnswer.user, user).fetchJoin()
                    .join(greenRoomQuestionAnswer.greenRoomQuestion, greenRoomQuestion).fetchJoin()
                .where(
                    greenRoomQuestionAnswer.user.id.eq(userId)
                )
                .orderBy(greenRoomQuestionAnswer.id.desc())
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
}
