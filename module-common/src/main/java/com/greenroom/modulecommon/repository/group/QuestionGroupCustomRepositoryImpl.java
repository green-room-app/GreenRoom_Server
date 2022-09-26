package com.greenroom.modulecommon.repository.group;

import com.greenroom.modulecommon.entity.group.QuestionGroup;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.category.QCategory.category;
import static com.greenroom.modulecommon.entity.group.QQuestionGroup.questionGroup;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class QuestionGroupCustomRepositoryImpl implements QuestionGroupCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<QuestionGroup> find(Long id) {
        QuestionGroup result = jpaQueryFactory
                .selectFrom(questionGroup)
                    .leftJoin(questionGroup.user, user).fetchJoin()
                    .join(questionGroup.category, category).fetchJoin()
                .where(questionGroup.id.eq(id))
                .fetchFirst();

        return Optional.ofNullable(result);
    }

    @Override
    public List<QuestionGroup> findAll(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(questionGroup)
                    .leftJoin(questionGroup.user, user).fetchJoin()
                    .join(questionGroup.category, category).fetchJoin()
                .where(questionGroup.user.id.eq(userId),
                        questionGroup.isDeleted.eq(false)
                )
                .orderBy(questionGroup.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }
}
