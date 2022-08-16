package com.greenroom.modulecommon.entity.scrap;

import com.greenroom.modulecommon.entity.AuditingCreateUpdateEntity;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "scraps")
public class Scrap extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_question_id")
    private UserQuestion userQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean used;

    public void delete() {
        this.used = false;
    }

    public void enable() {
        this.used = true;
    }

    @Builder
    private Scrap(Long id, UserQuestion userQuestion, User user) {
        checkArgument(userQuestion != null, "userQuestion 값은 필수입니다.");
        checkArgument(user != null, "user 값은 필수입니다.");

        this.id = id;
        this.userQuestion = userQuestion;
        this.user = user;
        this.used = true;
    }
}
