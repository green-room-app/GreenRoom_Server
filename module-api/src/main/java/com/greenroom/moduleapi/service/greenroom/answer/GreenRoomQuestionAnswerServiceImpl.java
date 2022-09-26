package com.greenroom.moduleapi.service.greenroom.answer;

import com.greenroom.moduleapi.service.greenroom.question.GreenRoomQuestionService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.greenroom.answer.GreenRoomQuestionAnswerRepository;
import com.greenroom.modulecommon.util.KeywordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.QuestionAnswer.ANSWER_LENGTH;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GreenRoomQuestionAnswerServiceImpl implements GreenRoomQuestionAnswerService {

    private final GreenRoomQuestionAnswerRepository answerRepository;
    private final GreenRoomQuestionService greenRoomQuestionService;
    private final UserService userService;

    @Override
    @Transactional
    public Long create(Long questionId, Long userId, String answer, List<String> keywordList) {
        checkArgument(questionId != null, "questionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(answer != null, "answer 값은 필수입니다.");
        checkArgument(answer.length() <= ANSWER_LENGTH, String.format("answer 길이는 %s자 이하여야 합니다.", ANSWER_LENGTH));

        if (isParticipated(questionId, userId)) {
            throw new IllegalStateException("이미 등록한 답변이 있습니다.");
        }

        GreenRoomQuestion greenRoomQuestion = greenRoomQuestionService.getGreenRoomQuestion(questionId);
        User user = userService.getUser(userId);
        String keywords = KeywordUtils.toKeywords(keywordList);

        GreenRoomQuestionAnswer greenRoomQuestionAnswer = GreenRoomQuestionAnswer.builder()
                .greenRoomQuestion(greenRoomQuestion)
                .answer(answer)
                .keywords(keywords)
                .user(user)
                .build();

        return answerRepository.save(greenRoomQuestionAnswer).getId();
    }

    @Override
    public List<GreenRoomQuestionAnswer> getAnswers(List<Long> categoryIds, Long userId, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return answerRepository.findAll(categoryIds, userId, pageable);
    }

    @Override
    public List<GreenRoomQuestionAnswer> getAnswers(Long questionId, Long userId, Pageable pageable) {
        checkArgument(questionId != null, "questionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return answerRepository.findAll(questionId, userId, pageable);
    }

    @Override
    public GreenRoomQuestionAnswer getAnswer(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        return answerRepository.find(id)
                .orElseThrow(
                        () -> new ApiException(NOT_FOUND, GreenRoomQuestionAnswer.class, String.format("id = %s", id))
                );
    }

    @Override
    public GreenRoomQuestionAnswer getAnswer(Long questionId, Long userId) {
        checkArgument(questionId != null, "questionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return answerRepository.find(questionId, userId)
                .orElseThrow(
                        () -> new ApiException(NOT_FOUND,
                                GreenRoomQuestionAnswer.class,
                                String.format("questionId = %s, userId = %s", questionId, userId))
                );
    }

    @Override
    public boolean isParticipated(Long questionId, Long userId) {
        checkArgument(questionId != null, "questionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return answerRepository.exist(questionId, userId);
    }
}
