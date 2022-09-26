package com.greenroom.modulecommon.repository.greenroom.answer.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestion.greenRoomQuestion;
import static com.greenroom.modulecommon.entity.greenroom.QGreenRoomQuestionAnswer.greenRoomQuestionAnswer;
import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
@Repository
public class GreenRoomQuestionAnswerQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<String> getUserProfileImages(Long questionId, int size) {
        return jpaQueryFactory.select(greenRoomQuestionAnswer.user.profileImage)
            .from(greenRoomQuestionAnswer)
                .join(greenRoomQuestionAnswer.greenRoomQuestion, greenRoomQuestion)
                .join(greenRoomQuestionAnswer.user, user)
            .where(greenRoomQuestion.id.eq(questionId))
                .limit(size)
            .fetch();
    }
}
