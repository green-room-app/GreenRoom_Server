package com.greenroom.moduleapi.service.interview;

import com.greenroom.moduleapi.service.category.CategoryService;
import com.greenroom.moduleapi.service.greenroom.answer.GreenRoomQuestionAnswerService;
import com.greenroom.moduleapi.service.greenroom.question.GreenRoomQuestionService;
import com.greenroom.moduleapi.service.group.QuestionGroupService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionAnswer;
import com.greenroom.modulecommon.entity.group.QuestionGroup;
import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.greenroom.modulecommon.entity.interview.QuestionType;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.interview.InterviewQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.Question.QUESTION_LENGTH;
import static com.greenroom.modulecommon.constant.EntityConstant.QuestionAnswer.ANSWER_LENGTH;
import static com.greenroom.modulecommon.constant.EntityConstant.QuestionAnswer.KEYWORDS_LENGTH;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class InterviewQuestionServiceImpl implements InterviewQuestionService {

    private final InterviewQuestionRepository interviewQuestionRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final QuestionGroupService groupService;
    private final GreenRoomQuestionService greenRoomQuestionService;
    private final GreenRoomQuestionAnswerService greenRoomQuestionAnswerService;

    @Override
    @Transactional
    public Long createByGreenRoom(Long groupId, Long greenRoomQuestionId, Long userId) {
        checkArgument(groupId != null, "groupId 값은 필수입니다.");
        checkArgument(greenRoomQuestionId != null, "greenRoomQuestionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        User user = userService.getUser(userId);
        GreenRoomQuestion greenRoomQuestion = greenRoomQuestionService.getGreenRoomQuestion(greenRoomQuestionId);
        QuestionGroup group = groupService.getGroup(groupId);

        if (greenRoomQuestionAnswerService.exist(greenRoomQuestion.getId(), userId)) {
            GreenRoomQuestionAnswer answer = greenRoomQuestionAnswerService.getAnswer(greenRoomQuestion.getId(), userId);
            InterviewQuestion interviewQuestion = InterviewQuestion.ofGreenRoom(user, greenRoomQuestion.getCategory(), group, greenRoomQuestion.getQuestion(), answer.getAnswer());
            return interviewQuestionRepository.save(interviewQuestion).getId();
        }

        InterviewQuestion interviewQuestion = InterviewQuestion.ofGreenRoom(user, greenRoomQuestion.getCategory(), group, greenRoomQuestion.getQuestion(), EMPTY);
        return interviewQuestionRepository.save(interviewQuestion).getId();
    }

    @Override
    @Transactional
    public Long createByBasicQuestion(Long groupId, Long basicQuestionId, Long userId) {
        checkArgument(groupId != null, "groupId 값은 필수입니다.");
        checkArgument(basicQuestionId != null, "basicQuestionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        User user = userService.getUser(userId);
        InterviewQuestion basicQuestion = getInterviewQuestion(basicQuestionId);

        if (!basicQuestion.getQuestionType().equals(QuestionType.BASIC_QUESTION)) {
            throw new IllegalArgumentException("기본 질문이 아닙니다");
        }

        QuestionGroup group = groupService.getGroup(groupId);

        InterviewQuestion interviewQuestion =
            InterviewQuestion.ofBasicQuestion(user, basicQuestion.getCategory(), group, basicQuestion.getQuestion());

        return interviewQuestionRepository.save(interviewQuestion).getId();
    }

    @Override
    @Transactional
    public Long createByMyQuestionWithGroup(Long groupId, Long myQuestionId, Long userId) {
        checkArgument(groupId != null, "groupId 값은 필수입니다.");
        checkArgument(myQuestionId != null, "myQuestionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        User user = userService.getUser(userId);
        InterviewQuestion myQuestion = getInterviewQuestion(myQuestionId);

        if (!myQuestion.getQuestionType().equals(QuestionType.MY_QUESTION)) {
            throw new IllegalArgumentException("그룹이 없는 마이질문이 아닙니다");
        }

        QuestionGroup group = groupService.getGroup(groupId);

        InterviewQuestion interviewQuestion = InterviewQuestion.ofMyQuestionWithGroup(user, myQuestion, group);

        return interviewQuestionRepository.save(interviewQuestion).getId();
    }

    @Override
    @Transactional
    public Long createMyQuestionWithoutGroup(Long categoryId, Long userId, String question) {
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(question != null, "question 값은 필수입니다.");
        checkArgument(question.length() <= QUESTION_LENGTH, String.format("question 길이는 %s자 이하여야 합니다.", QUESTION_LENGTH));

        Category category = categoryService.getCategory(categoryId);
        User user = userService.getUser(userId);

        InterviewQuestion interviewQuestion = InterviewQuestion.ofMyQuestionWithoutGroup(user, category, question);

        return interviewQuestionRepository.save(interviewQuestion).getId();
    }

    @Override
    public List<InterviewQuestion> getInterviewQuestions(Long groupId, Long userId, Pageable pageable) {
        checkArgument(groupId != null, "groupId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return interviewQuestionRepository.findAll(groupId, userId, pageable);
    }

    @Override
    public InterviewQuestion getInterviewQuestion(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        return interviewQuestionRepository.find(id)
            .orElseThrow(
                () -> new ApiException(NOT_FOUND, InterviewQuestion.class, String.format("id = %s", id))
            );
    }

    @Override
    @Transactional
    public Long updateQuestion(Long id, Long categoryId, String question) {
        checkArgument(id != null, "id 값은 필수입니다.");
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");
        checkArgument(
                question == null || question.length() <= QUESTION_LENGTH,
                String.format("question 길이는 %s자 이하여야 합니다.", QUESTION_LENGTH));

        InterviewQuestion interviewQuestion = getInterviewQuestion(id);
        Category category = categoryService.getCategory(categoryId);

        interviewQuestion.updateQuestion(category, question);

        return interviewQuestion.getId();
    }

    @Override
    @Transactional
    public Long updateAnswerAndKeywords(Long id, String answer, List<String> keywordList) {
        checkArgument(id != null, "id 값은 필수입니다.");
        checkArgument(
                answer == null || answer.length() <= ANSWER_LENGTH,
                String.format("answer 길이는 %s자 이하여야 합니다.", ANSWER_LENGTH));

        String keywords = toKeywords(keywordList);
        checkArgument(keywords.length() <= KEYWORDS_LENGTH,
                String.format("keywords 길이는 %s자 이하여야 합니다.", KEYWORDS_LENGTH));

        InterviewQuestion interviewQuestion = getInterviewQuestion(id);
        interviewQuestion.updateAnswerAndKeywords(answer, keywords);

        return interviewQuestion.getId();
    }

    @Override
    public List<String> toKeywordList(String keywords) {
        if (isEmpty(keywords)) {
            return Collections.emptyList();
        }

        return Arrays.stream(keywords.split("-"))
                .collect(toList());
    }

    @Override
    public String toKeywords(List<String> keywordList) {
        if (keywordList == null) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder();

        keywordList.forEach(keyword -> {
            sb.append(keyword).append("-");
        });

        String keywords = sb.toString();
        return keywords.substring(0, keywords.length() - 1);
    }

    @Override
    @Transactional
    public void updateGroups(Long groupId, List<Long> ids) {
        checkArgument(groupId != null, "groupId 값은 필수입니다.");
        checkArgument(ids != null, "ids 값은 필수입니다.");

        QuestionGroup group = groupService.getGroup(groupId);

        for (Long id : ids) {
            InterviewQuestion interviewQuestion = getInterviewQuestion(id);
            interviewQuestion.updateGroup(group);
        }
    }

    @Override
    @Transactional
    public Long updateGroup(Long id, Long groupId) {
        checkArgument(id != null, "id 값은 필수입니다.");
        checkArgument(groupId != null, "groupId 값은 필수입니다.");

        InterviewQuestion interviewQuestion = getInterviewQuestion(id);
        QuestionGroup group = groupService.getGroup(groupId);
        interviewQuestion.updateGroup(group);

        return interviewQuestion.getId();
    }

    @Override
    public boolean isOwner(Long id, Long userId) {
        checkArgument(id != null, "id 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        InterviewQuestion interviewQuestion = getInterviewQuestion(id);

        return interviewQuestion.getUser().map(user -> userId.equals(user.getId()))
            .orElse(false);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        InterviewQuestion interviewQuestion = getInterviewQuestion(id);

        interviewQuestion.delete();
    }
}
