package com.greenroom.modulecommon.repository.question.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.greenroom.modulecommon.entity.answer.QRefQuestionAnswer.refQuestionAnswer;
import static com.greenroom.modulecommon.entity.question.QRefQuestion.refQuestion;
import static com.greenroom.modulecommon.entity.user.QUser.user;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
@Repository
public class RefQuestionQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<QuestionQueryDto> findAll(Long userId, List<Long> categories, String title, Pageable pageable) {
        return jpaQueryFactory
                .select(
                    Projections.constructor(QuestionQueryDto.class,
                        refQuestionAnswer.id,
                        refQuestion.id,
                        refQuestion.question,
                        refQuestion.category.name,
                        refQuestionAnswer.answer,
                        refQuestionAnswer.keywords
                    )
                )
                .from(refQuestionAnswer)
                    .join(refQuestionAnswer.user, user)
                        .on(refQuestionAnswer.user.id.eq(userId))
                    .rightJoin(refQuestionAnswer.refQuestion, refQuestion)
                .where(
                    categoriesEq(categories),
                    titleContains(title)
                )
                .orderBy(refQuestionAnswer.refQuestion.id.asc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    private BooleanExpression categoriesEq(List<Long> categories) {
        if (isEmpty(categories)) {
            return null;
        }
        return refQuestion.category.id.in(categories);
    }

    private BooleanExpression titleContains(String title) {
        if (isBlank(title)) {
            return null;
        }
        return refQuestion.question.contains(title);
    }
}
