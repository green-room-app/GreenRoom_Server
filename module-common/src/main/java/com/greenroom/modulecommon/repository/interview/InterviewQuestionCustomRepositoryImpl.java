package com.greenroom.modulecommon.repository.interview;

import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.category.QCategory.category;
import static com.greenroom.modulecommon.entity.group.QQuestionGroup.questionGroup;
import static com.greenroom.modulecommon.entity.interview.QInterviewQuestion.interviewQuestion;
import static com.greenroom.modulecommon.entity.interview.QuestionType.MY_QUESTION;
import static com.greenroom.modulecommon.entity.interview.QuestionType.MY_QUESTION_WITH_GROUP;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class InterviewQuestionCustomRepositoryImpl implements InterviewQuestionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * B10 화면 마이 질문리스트 조회용 쿼리
     */
    @Override
    public List<InterviewQuestion> findAllMyQuestions(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(interviewQuestion)
                    .leftJoin(interviewQuestion.group, questionGroup).fetchJoin()
                    .join(interviewQuestion.category, category).fetchJoin()
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

    @Override
    public List<InterviewQuestion> findAll(Long groupId, Long userId, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(interviewQuestion)
                    .join(interviewQuestion.group, questionGroup).fetchJoin()
                    .join(interviewQuestion.category, category).fetchJoin()
                .where(
                    interviewQuestion.group.id.eq(groupId),
                    interviewQuestion.user.id.eq(userId),
                    interviewQuestion.isDeleted.eq(false)
                )
                .orderBy(interviewQuestion.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    @Override
    public Optional<InterviewQuestion> find(Long id) {
        InterviewQuestion result = jpaQueryFactory
                .selectFrom(interviewQuestion)
                    .leftJoin(interviewQuestion.user, user).fetchJoin()
                    .leftJoin(interviewQuestion.group, questionGroup).fetchJoin()
                    .join(interviewQuestion.category, category)
                .where(
                    interviewQuestion.id.eq(id),
                    interviewQuestion.isDeleted.eq(false)
                )
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    private BooleanExpression isMyQuestion() {
        return interviewQuestion.questionType.in(MY_QUESTION, MY_QUESTION_WITH_GROUP);
    }
}
