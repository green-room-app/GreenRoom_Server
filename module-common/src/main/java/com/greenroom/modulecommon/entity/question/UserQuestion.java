package com.greenroom.modulecommon.entity.question;

import com.greenroom.modulecommon.entity.AuditingCreateEntity;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.UserQuestion.QUESTION_LENGTH;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "user_questions")
public class UserQuestion extends AuditingCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questioner_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private QuestionType questionType;

    private String question;

    private LocalDateTime expiredAt;

    private int participants;

    private boolean used;

    public int incrementAndGetParticipants() {
        return ++participants;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    public void update(Category category, String question) {
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(isNotEmpty(question), "question 값은 필수입니다.");
        checkArgument(question.length() <= QUESTION_LENGTH,
                String.format("question 값은 %s자 이하여야 합니다.", QUESTION_LENGTH));

        this.category = category;
        this.question = question;
    }

    public void delete() {
        this.used = false;
    }

    public void enable() {
        this.used = true;
    }

    @Builder
    private UserQuestion(Long id,
                         User user,
                         Category category,
                         QuestionType questionType,
                         String question,
                         LocalDateTime expiredAt) {

        checkArgument(user != null, "user 값은 필수입니다.");
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(questionType != null, "questionType 값은 필수입니다.");
        checkArgument(isNotEmpty(question), "question 값은 필수입니다.");
        checkArgument(question.length() <= QUESTION_LENGTH,
                String.format("question 값은 %s자 이하여야 합니다.", QUESTION_LENGTH));
        checkArgument(expiredAt != null, "expiredAt 값은 필수입니다.");

        this.id = id;
        this.user = user;
        this.category = category;
        this.questionType = questionType;
        this.question = question;
        this.expiredAt = expiredAt;
        this.participants = 0;
        this.used = true;
    }
}
