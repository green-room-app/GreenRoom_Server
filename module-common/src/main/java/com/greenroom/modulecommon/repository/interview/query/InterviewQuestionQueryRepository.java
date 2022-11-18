package com.greenroom.modulecommon.repository.interview.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.greenroom.modulecommon.entity.interview.QInterviewQuestion.interviewQuestion;
import static com.greenroom.modulecommon.entity.interview.QuestionType.*;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
@Repository
public class InterviewQuestionQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * C2 화면 면접 질문 조회용 쿼리
     *
     * private Long id;
     * private Long categoryId;
     * private String categoryName;
     * private QuestionType questionType;
     * private String question;
     */
    public Page<InterviewQuestionQueryDto> findAll(Long userId,
                                                   List<Long> categories,
                                                   String title,
                                                   Pageable pageable) {

        List<InterviewQuestionQueryDto> contents = jpaQueryFactory
                .select(
                        Projections.constructor(
                            InterviewQuestionQueryDto.class,
                            interviewQuestion.id,
                            interviewQuestion.category.id,
                            interviewQuestion.category.name,
                            interviewQuestion.questionType,
                            interviewQuestion.question
                        )
                )
                .from(interviewQuestion)
                .where(
                    categoriesEq(categories),
                    titleContains(title),
                    isInterviewQuestion(userId),
                    interviewQuestion.isDeleted.eq(false)
                )
                .orderBy(interviewQuestion.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();

        Long count = getCount(userId, categories, title);

        return new PageImpl<>(contents, pageable, count);
    }

    private Long getCount(Long userId,
                          List<Long> categories,
                          String title) {

        return (long)jpaQueryFactory
                .selectFrom(interviewQuestion)
                .where(
                        categoriesEq(categories),
                        titleContains(title),
                        isInterviewQuestion(userId),
                        interviewQuestion.isDeleted.eq(false)
                )
                .fetch().size();
    }

    private BooleanExpression categoriesEq(List<Long> categories) {
        if (isEmpty(categories)) {
            return null;
        }

        return interviewQuestion.category.id.in(categories);
    }

    private BooleanExpression titleContains(String title) {
        if (isBlank(title)) {
            return null;
        }

        return interviewQuestion.question.contains(title);
    }

    /**
     * 기본 질문이거나, 특정 사용자가 만든, 그룹에 포함되지 않은 질문
     *     BASIC_QUESTION(0, "기본 질문"),
     *     BASIC_QUESTION_WITH_GROUP(1, "그룹에 담은 기본 질문"),
     *     GREENROOM_QUESTION(2, "그린룸 질문"),
     *     MY_QUESTION(3, "질문 리스트"),
     *     MY_QUESTION_WITH_GROUP(4, "그룹에 담은 질문 리스트");
     */
    private BooleanExpression isInterviewQuestion(Long userId) {
        return interviewQuestion.questionType.eq(BASIC_QUESTION)
                .or(interviewQuestion.user.id.eq(userId)
                        .and(interviewQuestion.questionType.eq(MY_QUESTION))
                );
    }
}
