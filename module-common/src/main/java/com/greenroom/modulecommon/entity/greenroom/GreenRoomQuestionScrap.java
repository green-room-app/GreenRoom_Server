package com.greenroom.modulecommon.entity.greenroom;

import com.greenroom.modulecommon.entity.AuditingCreateUpdateEntity;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "greenroom_question_scraps")
public class GreenRoomQuestionScrap extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private GreenRoomQuestion greenRoomQuestion;

    private boolean isDeleted;

    public void delete() {
        this.isDeleted = true;
    }

    public void enable() {
        this.isDeleted = false;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    @Builder
    private GreenRoomQuestionScrap(Long id, User user, GreenRoomQuestion greenRoomQuestion) {
        checkArgument(user != null, "user 값은 필수입니다.");
        checkArgument(greenRoomQuestion != null, "publicQuestion 값은 필수입니다.");

        this.id = id;
        this.user = user;
        this.greenRoomQuestion = greenRoomQuestion;
        this.isDeleted = false;
    }
}
