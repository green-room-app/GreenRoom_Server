package com.greenroom.moduleapi.service.answer;

import com.greenroom.moduleapi.service.question.UserQuestionService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.answer.UserQuestionAnswer;
import com.greenroom.modulecommon.entity.question.QuestionType;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.answer.UserQuestionAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.UserQuestionAnswer.ANSWER_LENGTH;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserQuestionAnswerServiceImpl implements UserQuestionAnswerService {

    private final UserQuestionAnswerRepository userQuestionAnswerRepository;
    private final UserQuestionService userQuestionService;
    private final UserService userService;

    @Override
    @Transactional
    public Long createPrivateQuestion(Long userId, String question, Long categoryId) {
        Long userQuestionId = userQuestionService.createPrivateQuestion(userId, question, categoryId);
        UserQuestion userQuestion = userQuestionService.getUserQuestion(userQuestionId);
        User user = userService.getUser(userId);

        UserQuestionAnswer userQuestionAnswer = UserQuestionAnswer.builder()
                                                                .userQuestion(userQuestion)
                                                                .user(user)
                                                                .answer(EMPTY)
                                                                .build();

        userQuestionAnswerRepository.save(userQuestionAnswer);

        return userQuestionId;
    }

    @Override
    @Transactional
    public Long createPrivateQuestionAnswer(Long userQuestionId, Long userId, String answer) {
        checkArgument(isNotEmpty(answer), "answer 값은 필수입니다.");
        checkArgument(answer.length() <= ANSWER_LENGTH, String.format("answer 값은 %s자 이하여야 합니다.", ANSWER_LENGTH));

        UserQuestionAnswer userQuestionAnswer = getUserQuestionAnswer(userQuestionId, userId);

        if (userQuestionAnswer.getUserQuestion().getQuestionType().equals(QuestionType.PUBLIC)) {
            throw new IllegalArgumentException("그린룸 질문 답변은 수정할 수 없습니다.");
        }

        userQuestionAnswer.update(answer);

        return userQuestionAnswer.getId();
    }

    @Override
    public List<UserQuestionAnswer> getUserQuestionAnswers(Long userId, QuestionType questionType, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(questionType != null, "questionType 값은 필수입니다.");

        return userQuestionAnswerRepository.findAllUserQuestionAnswer(userId, questionType, pageable);
    }

    @Override
    public UserQuestionAnswer getUserQuestionAnswer(Long userQuestionId, Long userId) {
        checkArgument(userQuestionId != null, "userQuestionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return userQuestionAnswerRepository.find(userQuestionId, userId)
            .orElseThrow(() ->  new ApiException(NOT_FOUND, UserQuestionAnswer.class,
                String.format("userQuestionId = %s, userId = %s", userQuestionId, userId)));
    }

    @Override
    public UserQuestionAnswer getUserQuestionAnswer(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        return userQuestionAnswerRepository.find(id)
                .orElseThrow(() ->  new ApiException(NOT_FOUND, UserQuestionAnswer.class, String.format("id = %s", id)));
    }

    @Override
    public boolean isWriter(Long id, Long userId) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        UserQuestionAnswer userQuestionAnswer = getUserQuestionAnswer(id);
        return userId.equals(userQuestionAnswer.getUser().getId());
    }

    @Override
    @Transactional
    public void update(Long id, String answer) {
        checkArgument(isNotEmpty(answer), "answer 값은 필수입니다.");
        checkArgument(answer.length() <= ANSWER_LENGTH, String.format("answer 값은 %s자 이하여야 합니다.", ANSWER_LENGTH));

        UserQuestionAnswer userQuestionAnswer = getUserQuestionAnswer(id);

        if (userQuestionAnswer.getUserQuestion().getQuestionType().equals(QuestionType.PUBLIC)) {
            throw new IllegalArgumentException("그린룸 질문 답변은 수정할 수 없습니다.");
        }

        userQuestionAnswer.update(answer);
    }
}
