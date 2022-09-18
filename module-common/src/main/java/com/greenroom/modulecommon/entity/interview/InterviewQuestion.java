package com.greenroom.modulecommon.entity.interview;

import com.greenroom.modulecommon.entity.AuditingCreateUpdateEntity;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.group.QuestionGroup;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.Question.QUESTION_LENGTH;
import static com.greenroom.modulecommon.constant.EntityConstant.QuestionAnswer.ANSWER_LENGTH;
import static com.greenroom.modulecommon.constant.EntityConstant.QuestionAnswer.KEYWORDS_LENGTH;
import static com.greenroom.modulecommon.entity.interview.QuestionType.*;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "interview_questions")
public class InterviewQuestion extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private QuestionGroup group;

    private QuestionType questionType;

    private String question;

    private String answer;

    private String keywords;

    private boolean isDeleted;

    public void delete() {
        this.isDeleted = true;
    }

    public void updateQuestion(Category category, String question) {
        checkArgument(category != null, "category 값은 필수입니다.");

        checkArgument(
                question == null || question.length() <= QUESTION_LENGTH,
                String.format("question 길이는 %s자 이하여야 합니다.", QUESTION_LENGTH));

        this.category = category;
        this.question = question;
    }

    public void updateGroup(QuestionGroup group) {
        checkArgument(group != null, "group 값은 필수입니다.");

        this.group = group;
    }

    public void updateAnswerAndKeywords(String answer, String keywords) {
        checkArgument(
                isEmpty(answer) || answer.length() <= ANSWER_LENGTH,
                String.format("answer 길이는 %s자 이하여야 합니다.", ANSWER_LENGTH));
        checkArgument(
                isEmpty(keywords) || keywords.length() <= KEYWORDS_LENGTH,
                String.format("keywords 길이는 %s자 이하여야 합니다.", KEYWORDS_LENGTH));

        this.answer = isEmpty(answer) ? this.answer : answer;
        this.keywords = isEmpty(keywords) ? this.keywords : keywords;
    }

    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    public Optional<QuestionGroup> getGroup() {
        return Optional.ofNullable(group);
    }

    public List<String> toKeywordList() {
        if (isEmpty(keywords)) {
            return Collections.emptyList();
        }

        return Arrays.stream(keywords.split("-"))
                .collect(toList());
    }

    /**
     * 그린룸 질문 그룹에 담기
     */
    public static InterviewQuestion ofGreenRoom(User user, Category category, QuestionGroup group, String question, String answer) {
        return InterviewQuestion.builder()
                .user(user)
                .category(category)
                .group(group)
                .questionType(GREENROOM_QUESTION)
                .question(question)
                .answer(answer)
                .keywords(null)
                .build();
    }

    public static InterviewQuestion ofBasicQuestion(User user, Category category, QuestionGroup group, String question) {
        return InterviewQuestion.builder()
                .user(user)
                .category(category)
                .group(group)
                .questionType(BASIC_QUESTION_WITH_GROUP)
                .question(question)
                .answer(null)
                .keywords(null)
                .build();
    }

    /**
     * 그룹없는 마이질문리스트 생성
     */
    public static InterviewQuestion ofMyQuestionWithoutGroup(User user, Category category, String question) {
        return InterviewQuestion.builder()
                .user(user)
                .category(category)
                .group(null)
                .questionType(MY_QUESTION)
                .question(question)
                .answer(null)
                .keywords(null)
                .build();
    }

    /**
     * 그룹없는 마이질문리스트 => 그룹에 포함된 마이질문리스트 생성
     */
    public static InterviewQuestion ofMyQuestionWithGroup(User user, InterviewQuestion myQuestion, QuestionGroup group) {
        return InterviewQuestion.builder()
                .user(user)
                .category(myQuestion.getCategory())
                .group(group)
                .questionType(MY_QUESTION_WITH_GROUP)
                .question(myQuestion.getQuestion())
                .answer(myQuestion.getAnswer())
                .keywords(myQuestion.getKeywords())
                .build();
    }

    @Builder
    private InterviewQuestion(Long id,
                              User user,
                              Category category,
                              QuestionGroup group,
                              QuestionType questionType,
                              String question,
                              String answer,
                              String keywords) {

        checkArgument(user != null, "user 값은 필수입니다.");
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(questionType != null, "questionType 값은 필수입니다.");
        checkArgument(isNotEmpty(question), "question 값은 필수입니다.");
        checkArgument(question.length() <= QUESTION_LENGTH,
                String.format("question 길이는 %s자 이하여야 합니다.", QUESTION_LENGTH));

        this.id = id;
        this.user = user;
        this.category = category;
        this.group = group;
        this.questionType = questionType;
        this.question = question;
        this.answer = answer;
        this.keywords = keywords;
        this.isDeleted = false;
    }
}
