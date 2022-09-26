package com.greenroom.modulecommon.entity.greenroom;

import com.greenroom.modulecommon.entity.AuditingCreateEntity;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.QuestionAnswer.ANSWER_LENGTH;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "greenroom_question_answers")
public class GreenRoomQuestionAnswer extends AuditingCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private GreenRoomQuestion greenRoomQuestion;

    private String answer;

    private String keywords;

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    @Builder
    private GreenRoomQuestionAnswer(Long id,
                                    User user,
                                    GreenRoomQuestion greenRoomQuestion,
                                    String answer,
                                    String keywords) {

        checkArgument(user != null, "user 값은 필수입니다.");
        checkArgument(greenRoomQuestion != null, "publicQuestion 값은 필수입니다.");
        checkArgument(isNotEmpty(answer), "answer 값은 필수입니다.");
        checkArgument(answer.length() <= ANSWER_LENGTH,
                String.format("answer 길이는 %s자 이하여야 합니다.", ANSWER_LENGTH));

        this.id = id;
        this.user = user;
        this.greenRoomQuestion = greenRoomQuestion;
        this.answer = answer;
        this.keywords = keywords;
    }
}
