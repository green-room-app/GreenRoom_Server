package com.greenroom.modulecommon.repository.interview.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.greenroom.modulecommon.entity.group.QQuestionGroup.questionGroup;
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
    public List<InterviewQuestionQueryDto> findAll(Long userId,
                                                   List<Long> categories,
                                                   String title,
                                                   Pageable pageable) {

        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                InterviewQuestionQueryDto.class,
                                interviewQuestion.id,
                                interviewQuestion.category.id,
                                interviewQuestion.category.name,
                                interviewQuestion.questionType,
                                interviewQuestion.question
                        )
                ).from(interviewQuestion)
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
    }

    /**
     * B10 화면 마이 질문리스트 조회용 쿼리
     *
     * private Long id;
     * private String groupCategoryName;
     * private String groupName;
     * private String questionCategoryName;
     * private QuestionType questionType;
     * private String question;
     */
    public List<MyQuestionQueryDto> findAll(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                            MyQuestionQueryDto.class,
                            interviewQuestion.id,
                            interviewQuestion.group == null ? null : interviewQuestion.group.category.name,
                            interviewQuestion.group == null ? null : interviewQuestion.group.name,
                            interviewQuestion.category.name,
                            interviewQuestion.questionType,
                            interviewQuestion.question
                        )
                ).from(interviewQuestion)
                    .leftJoin(interviewQuestion.group, questionGroup)
                .where(
                    interviewQuestion.user.id.eq(userId),
                    isMyQuestion(),
                    interviewQuestion.isDeleted.eq(false)
                )
                .orderBy(interviewQuestion.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
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

    private BooleanExpression isMyQuestion() {
        return interviewQuestion.questionType.in(MY_QUESTION, MY_QUESTION_WITH_GROUP);
    }
}
