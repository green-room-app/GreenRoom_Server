package com.greenroom.modulecommon.entity.group;

import com.greenroom.modulecommon.entity.AuditingCreateUpdateEntity;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.Group.NAME_LENGTH;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "question_groups")
public class QuestionGroup extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;

    private boolean isDeleted;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<InterviewQuestion> interviewQuestions = new ArrayList<>();

    public void delete() {
        this.isDeleted = true;
    }

    public void update(Category category, String name) {
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH,
                String.format("name 길이는 %s자 이하여야 합니다.", NAME_LENGTH));

        this.category = category;
        this.name = name;
    }

    public void update(Category category) {
        checkArgument(category != null, "category 값은 필수입니다.");

        this.category = category;
    }

    public void update(String name) {
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH,
                String.format("name 길이는 %s자 이하여야 합니다.", NAME_LENGTH));

        this.name = name;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    @Builder
    private QuestionGroup(Long id,
                          User user,
                          Category category,
                          String name) {

        checkArgument(user != null, "user 값은 필수입니다.");
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH,
                String.format("name 길이는 %s자 이하여야 합니다.", NAME_LENGTH));

        this.id = id;
        this.user = user;
        this.category = category;
        this.name = name;
        this.isDeleted = false;
    }
}
