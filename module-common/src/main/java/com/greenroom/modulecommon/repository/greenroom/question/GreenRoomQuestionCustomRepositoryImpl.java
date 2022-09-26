package com.greenroom.modulecommon.repository.greenroom.question;

import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.greenroom.modulecommon.entity.category.QCategory.category;
import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestion.greenRoomQuestion;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class GreenRoomQuestionCustomRepositoryImpl implements GreenRoomQuestionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 내가 개설한 그린룸 조회
     */
    @Override
    public Slice<GreenRoomQuestion> findMyGreenRoomQuestions(Long userId, Pageable pageable) {
        List<GreenRoomQuestion> greenRoomQuestions = jpaQueryFactory
                .selectFrom(greenRoomQuestion)
                .leftJoin(greenRoomQuestion.user, user).fetchJoin()
                .join(greenRoomQuestion.category, category).fetchJoin()
                .where(
                        greenRoomQuestion.user.id.eq(userId),
                        greenRoomQuestion.isDeleted.eq(false)
                )
                .orderBy(greenRoomQuestion.id.desc())
                    .limit(pageable.getPageSize() + 1) // limit보다 데이터를 1개 더 들고오기
                    .offset(pageable.getOffset())
                .fetch();

        List<GreenRoomQuestion> contents = new ArrayList<>(greenRoomQuestions);

        boolean hasNext = false;

        if (contents.size() > pageable.getPageSize()) {
            contents.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    @Override
    public Optional<GreenRoomQuestion> find(Long id) {
        GreenRoomQuestion result = jpaQueryFactory
            .selectFrom(greenRoomQuestion)
                .leftJoin(greenRoomQuestion.user, user).fetchJoin()
                .join(greenRoomQuestion.category, category).fetchJoin()
            .where(
                greenRoomQuestion.id.eq(id),
                greenRoomQuestion.isDeleted.eq(false)
            )
            .fetchFirst();

        return Optional.ofNullable(result);
    }
}
