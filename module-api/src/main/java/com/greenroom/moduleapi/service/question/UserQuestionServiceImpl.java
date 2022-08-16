package com.greenroom.moduleapi.service.question;

import com.greenroom.moduleapi.service.category.CategoryService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.question.QuestionType;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.question.UserQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.UserQuestion.QUESTION_LENGTH;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserQuestionServiceImpl implements UserQuestionService {

    private final UserQuestionRepository userQuestionRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    @Override
    @Transactional
    public Long createPublicQuestion(Long userId, String question, Long categoryId, LocalDateTime expiredAt) {
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(isNotEmpty(question), "question 값은 필수입니다.");
        checkArgument(question.length() <= QUESTION_LENGTH, String.format("question 값은 %s자 이하여야 합니다.", QUESTION_LENGTH));
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");
        checkArgument(expiredAt != null, "expiredAt 값은 필수입니다.");

        User user = userService.getUser(userId);
        Category category = categoryService.getCategory(categoryId);

        UserQuestion userQuestion = UserQuestion.builder()
                                                .user(user)
                                                .category(category)
                                                .questionType(QuestionType.PUBLIC)
                                                .question(question)
                                                .expiredAt(expiredAt)
                                                .build();

        userQuestionRepository.save(userQuestion);

        return userQuestion.getId();
    }

    @Override
    @Transactional
    public Long createPrivateQuestion(Long userId, String question, Long categoryId) {
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(isNotEmpty(question), "question 값은 필수입니다.");
        checkArgument(question.length() <= QUESTION_LENGTH, String.format("question 값은 %s자 이하여야 합니다.", QUESTION_LENGTH));
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");

        User user = userService.getUser(userId);
        Category category = categoryService.getCategory(categoryId);

        UserQuestion userQuestion = UserQuestion.builder()
                                                .user(user)
                                                .category(category)
                                                .questionType(QuestionType.PRIVATE)
                                                .question(question)
                                                .expiredAt(LocalDateTime.now())
                                                .build();

        userQuestionRepository.save(userQuestion);

        return userQuestion.getId();
    }

    @Override
    public UserQuestion getUserQuestion(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        return userQuestionRepository.find(id)
            .orElseThrow(() ->  new ApiException(NOT_FOUND, UserQuestion.class, String.format("id = %s", id)));
    }

    @Override
    public List<UserQuestion> getUserQuestions(List<Long> categories, QuestionType questionType, Pageable pageable) {
        return userQuestionRepository.findAll(categories, questionType, pageable);
    }

    @Override
    public List<UserQuestion> getPopularQuestions(Pageable pageable) {
        return userQuestionRepository.findPopularQuestions(pageable);
    }

    @Override
    public boolean isWriter(Long id, Long userId) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        UserQuestion userQuestion = getUserQuestion(id);
        return userId.equals(userQuestion.getUser().getId());
    }

    @Override
    @Transactional
    public void update(Long id, String question, Long categoryId) {
        checkArgument(isNotEmpty(question), "question 값은 필수입니다.");
        checkArgument(question.length() <= QUESTION_LENGTH, String.format("question 값은 %s자 이하여야 합니다.", QUESTION_LENGTH));
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");

        UserQuestion userQuestion = getUserQuestion(id);

        if (userQuestion.getQuestionType().equals(QuestionType.PUBLIC)) {
            throw new IllegalArgumentException("그린룸 질문은 수정할 수 없습니다.");
        }

        Category category = categoryService.getCategory(categoryId);
        userQuestion.update(category, question);
    }

    @Override
    @Transactional
    public void deletePrivateQuestion(Long id) {
        UserQuestion userQuestion = getUserQuestion(id);

        if (userQuestion.getQuestionType().equals(QuestionType.PUBLIC)) {
            throw new IllegalArgumentException("그린룸 질문은 삭제할 수 없습니다.");
        }

        userQuestion.delete();
    }
}


