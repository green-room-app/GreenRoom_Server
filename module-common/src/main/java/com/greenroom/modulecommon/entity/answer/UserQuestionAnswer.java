package com.greenroom.modulecommon.entity.answer;

import com.greenroom.modulecommon.entity.AuditingCreateEntity;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.UserQuestionAnswer.ANSWER_LENGTH;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_question_answers")
public class UserQuestionAnswer extends AuditingCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_question_id")
    private UserQuestion userQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String answer;

    public void update(String answer) {
        checkArgument(isNotEmpty(answer), "answer 값은 필수입니다.");
        checkArgument(answer.length() <= ANSWER_LENGTH, String.format("answer 값은 %s자 이하여야 합니다.", ANSWER_LENGTH));

        this.answer = answer;
    }

    @Builder
    private UserQuestionAnswer(Long id, UserQuestion userQuestion, User user, String answer) {
        checkArgument(userQuestion != null, "userQuestion 값은 필수입니다.");
        checkArgument(user != null, "user 값은 필수입니다.");
        checkArgument(answer != null, "answer 값은 필수입니다.");
        checkArgument(answer.length() <= ANSWER_LENGTH, String.format("answer 값은 %s자 이하여야 합니다.", ANSWER_LENGTH));

        this.id = id;
        this.userQuestion = userQuestion;
        this.user = user;
        this.answer = answer;
    }
}
