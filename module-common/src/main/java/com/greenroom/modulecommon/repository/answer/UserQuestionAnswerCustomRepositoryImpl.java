package com.greenroom.modulecommon.repository.answer;

import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import com.greenroom.modulecommon.entity.question.QuestionType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.answer.QUserQuestionAnswer.userQuestionAnswer;
import static com.greenroom.modulecommon.entity.question.QUserQuestion.userQuestion;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class UserQuestionAnswerCustomRepositoryImpl implements UserQuestionAnswerCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<UserQuestionAnswer> findAllUserQuestionAnswer(Long userId, QuestionType questionType, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(userQuestionAnswer)
                    .join(userQuestionAnswer.user, user).fetchJoin()
                    .join(userQuestionAnswer.userQuestion, userQuestion).fetchJoin()
                .where(userQuestionAnswer.user.id.eq(userId),
                        userQuestionAnswer.userQuestion.questionType.eq(questionType),
                        userQuestionAnswer.userQuestion.used.eq(true)
                )
                .orderBy(userQuestionAnswer.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public Optional<UserQuestionAnswer> find(Long userQuestionId, Long userId) {
        UserQuestionAnswer result = jpaQueryFactory
            .selectFrom(userQuestionAnswer)
                .join(userQuestionAnswer.user, user).fetchJoin()
                .join(userQuestionAnswer.userQuestion, userQuestion).fetchJoin()
                .where(
                    userQuestionAnswer.userQuestion.id.eq(userQuestionId),
                    userQuestionAnswer.userQuestion.used.eq(true),
                    userQuestionAnswer.user.id.eq(userId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Optional<UserQuestionAnswer> find(Long id) {
        UserQuestionAnswer result = jpaQueryFactory
                .selectFrom(userQuestionAnswer)
                    .join(userQuestionAnswer.user, user).fetchJoin()
                    .join(userQuestionAnswer.userQuestion, userQuestion).fetchJoin()
                .where(userQuestionAnswer.id.eq(id),
                        userQuestionAnswer.userQuestion.used.eq(true)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
