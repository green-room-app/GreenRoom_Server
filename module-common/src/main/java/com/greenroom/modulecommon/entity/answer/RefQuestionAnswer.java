package com.greenroom.modulecommon.entity.answer;

import com.greenroom.modulecommon.entity.AuditingCreateUpdateEntity;
import com.greenroom.modulecommon.entity.question.RefQuestion;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.QuestionAnswer.ANSWER_LENGTH;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "ref_question_answers")
public class RefQuestionAnswer extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_question_id")
    private RefQuestion refQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String answer;

    private String keywords;

    @Builder
    private RefQuestionAnswer(Long id, RefQuestion refQuestion, User user, String answer) {
        checkArgument(refQuestion != null, "refQuestion 값은 필수입니다.");
        checkArgument(user != null, "user 값은 필수입니다.");
        checkArgument(isNotEmpty(answer), "answer 값은 필수입니다.");
        checkArgument(answer.length() <= ANSWER_LENGTH, String.format("answer 값은 %s자 이하여야 합니다.", ANSWER_LENGTH));

        this.id = id;
        this.refQuestion = refQuestion;
        this.user = user;
        this.answer = answer;
        this.keywords = EMPTY;
    }
}
