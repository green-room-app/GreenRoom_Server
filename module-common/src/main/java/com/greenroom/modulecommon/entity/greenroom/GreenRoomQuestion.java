package com.greenroom.modulecommon.entity.greenroom;

import com.greenroom.modulecommon.entity.AuditingCreateEntity;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.Question.QUESTION_LENGTH;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "greenroom_questions")
public class GreenRoomQuestion extends AuditingCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String question;

    private LocalDateTime expiredAt;

    private boolean isDeleted;

    @OneToMany(mappedBy = "greenRoomQuestion", cascade = CascadeType.ALL)
    private List<GreenRoomQuestionAnswer> questionAnswers = new ArrayList<>();

    public void delete() {
        this.isDeleted = true;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    @Builder
    private GreenRoomQuestion(Long id,
                              User user,
                              Category category,
                              String question,
                              LocalDateTime expiredAt) {

        checkArgument(user != null, "user 값은 필수입니다.");
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(isNotEmpty(question), "question 값은 필수입니다.");
        checkArgument(question.length() <= QUESTION_LENGTH,
                String.format("question 길이는 %s자 이하여야 합니다.", QUESTION_LENGTH));
        checkArgument(expiredAt != null, "expiredAt 값은 필수입니다.");

        this.id = id;
        this.user = user;
        this.category = category;
        this.question = question;
        this.expiredAt = expiredAt;
        this.isDeleted = false;
    }
}
