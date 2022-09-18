package com.greenroom.modulecommon.repository.interview;

import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.category.QCategory.category;
import static com.greenroom.modulecommon.entity.group.QQuestionGroup.questionGroup;
import static com.greenroom.modulecommon.entity.interview.QInterviewQuestion.interviewQuestion;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class InterviewQuestionCustomRepositoryImpl implements InterviewQuestionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

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
}
