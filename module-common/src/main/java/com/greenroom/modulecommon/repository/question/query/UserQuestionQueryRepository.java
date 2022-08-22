package com.greenroom.modulecommon.repository.question.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.greenroom.modulecommon.entity.answer.QUserQuestionAnswer.userQuestionAnswer;
import static com.greenroom.modulecommon.entity.question.QUserQuestion.userQuestion;
import static com.greenroom.modulecommon.entity.user.QUser.user;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
@Repository
public class UserQuestionQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<QuestionQueryDto> findAll(Long userId, List<Long> categories, String title, Pageable pageable) {
        return jpaQueryFactory
                .select(
                    Projections.constructor(QuestionQueryDto.class,
                        userQuestionAnswer.id,
                        userQuestion.id,
                        userQuestion.question,
                        userQuestion.category.name,
                        userQuestionAnswer.answer,
                        userQuestionAnswer.keywords
                    )
                )
                .from(userQuestionAnswer)
                    .join(userQuestionAnswer.user, user)
                        .on(userQuestionAnswer.user.id.eq(userId))
                    .join(userQuestionAnswer.userQuestion, userQuestion)
                .where(
                        categoriesEq(categories),
                        titleContains(title)
                )
                .orderBy(userQuestion.id.asc())
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

    private BooleanExpression titleContains(String title) {
        if (isBlank(title)) {
            return null;
        }
        return userQuestion.question.contains(title);
    }
}
