package com.greenroom.modulecommon.repository.greenroom.question.query;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.greenroom.modulecommon.entity.category.QCategory.category;
import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestion.greenRoomQuestion;
import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestionAnswer.greenRoomQuestionAnswer;
import static com.greenroom.modulecommon.entity.user.QUser.user;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@RequiredArgsConstructor
@Repository
public class GreenRoomQuestionQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * '직무(1개)'와 관련된 그린룸질문 목록 조회에 사용 (B2 화면)
     *
     * GET /api/green-questions
     */
    public List<GreenRoomQuestionQueryDto> findAll(Long userId, Long categoryId, Pageable pageable) {
        return jpaQueryFactory.select(
            Projections.constructor(
                GreenRoomQuestionQueryDto.class,
                greenRoomQuestion.id,
                greenRoomQuestion.user == null ? null : greenRoomQuestion.user.profileImage,
                greenRoomQuestion.user == null ? null : greenRoomQuestion.user.name,
                greenRoomQuestion.questionAnswers.size(),
                greenRoomQuestionAnswer.id.isNotNull(),
                greenRoomQuestion.category.name,
                greenRoomQuestion.question,
                greenRoomQuestionAnswer.answer,
                greenRoomQuestion.expiredAt
            )
        ).from(greenRoomQuestion)
            .join(greenRoomQuestion.category, category)
            .leftJoin(greenRoomQuestion.user, user)
            .leftJoin(greenRoomQuestionAnswer)
                .on(greenRoomQuestion.id.eq(greenRoomQuestionAnswer.greenRoomQuestion.id))
                .on(greenRoomQuestionAnswer.user.id.eq(userId))
        .where(
            greenRoomQuestion.category.id.eq(categoryId),
            greenRoomQuestion.expiredAt.after(LocalDateTime.now())
        )
        .orderBy(greenRoomQuestion.expiredAt.desc(), greenRoomQuestion.id.asc())
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
        .fetch();
    }

    /**
     *     private Long id;    //그린룸질문 id
     *     private String profileUrl;
     *     private String name;    //질문자 닉네임
     *     private int participants;
     *     private String categoryName;
     *     private String question;
     *     private LocalDateTime expiredAt;
     */
    public List<PopularQuestionQueryDto> findAllPopularQuestions(Pageable pageable) {
         return jpaQueryFactory.select(
            Projections.constructor(
                PopularQuestionQueryDto.class,
                greenRoomQuestion.id,
                greenRoomQuestion.user == null ? null : greenRoomQuestion.user.profileImage,
                greenRoomQuestion.user == null ? null : greenRoomQuestion.user.name,
                greenRoomQuestion.questionAnswers.size(),
                greenRoomQuestion.category.name,
                greenRoomQuestion.question,
                greenRoomQuestion.expiredAt
            )
        ).from(greenRoomQuestion)
            .join(greenRoomQuestion.category, category)
            .leftJoin(greenRoomQuestion.user, user)
        .where(
            greenRoomQuestion.expiredAt.after(LocalDateTime.now())
        )
        .orderBy(
            greenRoomQuestion.questionAnswers.size().desc(),
            greenRoomQuestion.expiredAt.asc()
        )
            .limit(pageable.getPageSize())
            .offset(pageable.getOffset())
        .fetch();
    }

    /**
     *  최근 그린룸질문 목록 조회에 사용 (B4 화면)
     *
     *     private Long id;    //그린룸질문 id
     *     private String profileUrl;
     *     private boolean isParticipated;
     *     private String categoryName;
     *     private String question;
     *     private LocalDateTime expiredAt;
     */
    public List<RecentQuestionQueryDto> findAllRecentQuestions(List<Long> categoryIds, Long userId, Pageable pageable) {
        return jpaQueryFactory
                .select(
                    Projections.constructor(
                        RecentQuestionQueryDto.class,
                        greenRoomQuestion.id,
                        greenRoomQuestion.user == null ? null : greenRoomQuestion.user.profileImage,
                        greenRoomQuestionAnswer.id.isNotNull(),
                        greenRoomQuestion.category.name,
                        greenRoomQuestion.question,
                        greenRoomQuestion.expiredAt
                    )
                )
                .from(greenRoomQuestion)
                    .join(greenRoomQuestion.category, category)
                    .leftJoin(greenRoomQuestion.user, user)
                    .leftJoin(greenRoomQuestionAnswer)
                        .on(greenRoomQuestion.id.eq(greenRoomQuestionAnswer.greenRoomQuestion.id))
                        .on(greenRoomQuestionAnswer.user.id.eq(userId))
                .where(
                    categoriesEq(categoryIds),
                    greenRoomQuestion.isDeleted.eq(false)
                )
                .orderBy(greenRoomQuestion.id.desc())
                    .limit(pageable.getPageSize())
                    .offset(pageable.getOffset())
                .fetch();
    }

    private BooleanExpression categoriesEq(List<Long> categoryIds) {
        if (isEmpty(categoryIds)) {
            return null;
        }

        return greenRoomQuestion.category.id.in(categoryIds);
    }
}
