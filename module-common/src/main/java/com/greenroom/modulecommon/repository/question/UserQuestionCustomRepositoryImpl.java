package com.greenroom.modulecommon.repository.question;

import com.greenroom.modulecommon.entity.question.QuestionType;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.category.QCategory.category;
import static com.greenroom.modulecommon.entity.question.QUserQuestion.userQuestion;
import static com.greenroom.modulecommon.entity.user.QUser.user;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@RequiredArgsConstructor
public class UserQuestionCustomRepositoryImpl implements UserQuestionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * FOR 최근질문 API
     * '직무 카테고리' 조건으로 필터링 가능
     * 최신 질문부터 내림차순으로 보여줌
     */
    @Override
    public List<UserQuestion> findAll(List<Long> categories, QuestionType questionType, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(userQuestion)
                    .leftJoin(userQuestion.user, user).fetchJoin()
                    .join(userQuestion.category, category).fetchJoin()
                .where(
                    categoriesEq(categories),
                    userQuestion.questionType.eq(questionType),
                    userQuestion.used.eq(true)
                )
                .orderBy(userQuestion.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public List<UserQuestion> findAll(Long userId, QuestionType questionType, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(userQuestion)
                    .join(userQuestion.user, user).fetchJoin()
                    .join(userQuestion.category, category).fetchJoin()
                .where(
                    userQuestion.user.id.eq(userId),
                    userQuestion.questionType.eq(questionType),
                    userQuestion.used.eq(true)
                )
                .orderBy(userQuestion.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public Optional<UserQuestion> find(Long id) {
        UserQuestion result = jpaQueryFactory
                .selectFrom(userQuestion)
                    .join(userQuestion.user, user).fetchJoin()
                    .join(userQuestion.category, category).fetchJoin()
                .where(userQuestion.id.eq(id),
                        userQuestion.used.eq(true)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<UserQuestion> findPopularQuestions(Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(userQuestion)
                    .join(userQuestion.user, user).fetchJoin()
                    .join(userQuestion.category, category).fetchJoin()
                .where(
                    userQuestion.expiredAt.after(LocalDateTime.now()),
                    userQuestion.questionType.eq(QuestionType.PUBLIC),
                    userQuestion.used.eq(true)
                )
                .orderBy(
                    userQuestion.participants.desc(),
                    userQuestion.expiredAt.desc()
                )
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    private BooleanExpression categoriesEq(List<Long> categories) {
        if (isEmpty(categories)) {
            return null;
        }
        return userQuestion.category.id.in(categories);
    }
}
