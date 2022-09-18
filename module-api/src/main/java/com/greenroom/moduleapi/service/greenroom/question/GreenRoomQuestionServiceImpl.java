package com.greenroom.moduleapi.service.greenroom.question;

import com.greenroom.moduleapi.service.category.CategoryService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.greenroom.question.GreenRoomQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.Question.QUESTION_LENGTH;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GreenRoomQuestionServiceImpl implements GreenRoomQuestionService {

    private final GreenRoomQuestionRepository greenRoomQuestionRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public Long create(Long userId, String question, Long categoryId, LocalDateTime expiredAt) {
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(question != null, "name 값은 필수입니다.");
        checkArgument(question.length() <= QUESTION_LENGTH, String.format("question 길이는 %s자 이하여야 합니다.", QUESTION_LENGTH));
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");
        checkArgument(expiredAt != null, "expiredAt 값은 필수입니다.");

        User user = userService.getUser(userId);
        Category category = categoryService.getCategory(categoryId);

        //FIXME: expiredAt 24시간이내 값인지 검증하기

        GreenRoomQuestion greenRoomQuestion = GreenRoomQuestion.builder()
                .question(question)
                .user(user)
                .category(category)
                .expiredAt(expiredAt)
                .build();

        return greenRoomQuestionRepository.save(greenRoomQuestion).getId();
    }

    @Override
    public List<GreenRoomQuestion> getGreenRoomQuestions(Long userId, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return greenRoomQuestionRepository.findAll(userId, pageable);
    }

    @Override
    public GreenRoomQuestion getGreenRoomQuestion(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        return greenRoomQuestionRepository.find(id)
                .orElseThrow(() -> new ApiException(NOT_FOUND, GreenRoomQuestion.class, String.format("id = %s", id)));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        GreenRoomQuestion greenRoomQuestion = getGreenRoomQuestion(id);
        greenRoomQuestion.delete();
    }
}
