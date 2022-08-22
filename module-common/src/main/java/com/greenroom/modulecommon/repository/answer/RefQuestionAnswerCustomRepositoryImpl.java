package com.greenroom.modulecommon.repository.answer;

import com.greenroom.modulecommon.entity.answer.RefQuestionAnswer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.greenroom.modulecommon.entity.answer.QRefQuestionAnswer.refQuestionAnswer;
import static com.greenroom.modulecommon.entity.question.QRefQuestion.refQuestion;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class RefQuestionAnswerCustomRepositoryImpl implements RefQuestionAnswerCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<RefQuestionAnswer> find(Long refQuestionId, Long userId) {
        RefQuestionAnswer result = jpaQueryFactory
                .selectFrom(refQuestionAnswer)
                    .join(refQuestionAnswer.user, user).fetchJoin()
                    .join(refQuestionAnswer.refQuestion, refQuestion).fetchJoin()
                .where(
                    refQuestionAnswer.refQuestion.id.eq(refQuestionId),
                    refQuestionAnswer.user.id.eq(userId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
