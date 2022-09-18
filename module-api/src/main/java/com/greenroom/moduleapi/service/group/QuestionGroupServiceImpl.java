package com.greenroom.moduleapi.service.group;

import com.greenroom.moduleapi.service.category.CategoryService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.group.QuestionGroup;
import com.greenroom.modulecommon.entity.interview.InterviewQuestion;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.group.QuestionGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.Group.NAME_LENGTH;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionGroupServiceImpl implements QuestionGroupService {

    private final QuestionGroupRepository groupRepository;

    private final CategoryService categoryService;
    private final UserService userService;

    @Override
    @Transactional
    public Long create(Long categoryId, Long userId, String name) {
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(name != null, "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH, String.format("name 길이는 %s자 이하여야 합니다.", NAME_LENGTH));

        Category category = categoryService.getCategory(categoryId);
        User user = userService.getUser(userId);

        QuestionGroup questionGroup = QuestionGroup.builder()
                .category(category)
                .user(user)
                .name(name)
                .build();

        return groupRepository.save(questionGroup).getId();
    }

    @Override
    public List<QuestionGroup> getGroups(Long userId, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return groupRepository.findAll(userId, pageable);
    }

    @Override
    public QuestionGroup getGroup(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        return groupRepository.find(id)
                .orElseThrow(() -> new ApiException(NOT_FOUND, QuestionGroup.class, String.format("id = %s", id)));
    }

    @Override
    @Transactional
    public Long update(Long id, Long categoryId, String name) {
        checkArgument(id != null, "id 값은 필수입니다.");
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");
        checkArgument(name != null, "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH, String.format("name 길이는 %s자 이하여야 합니다.", NAME_LENGTH));

        QuestionGroup group = getGroup(id);
        Category category = categoryService.getCategory(categoryId);
        group.update(category, name);

        return group.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, Long categoryId) {
        checkArgument(id != null, "id 값은 필수입니다.");
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");

        QuestionGroup group = getGroup(id);
        Category category = categoryService.getCategory(categoryId);
        group.update(category);

        return group.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, String name) {
        checkArgument(id != null, "id 값은 필수입니다.");
        checkArgument(name != null, "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH, String.format("name 길이는 %s자 이하여야 합니다.", NAME_LENGTH));

        QuestionGroup group = getGroup(id);
        group.update(name);

        return group.getId();
    }

    @Override
    public boolean isOwner(Long id, Long userId) {
        checkArgument(id != null, "id 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        QuestionGroup group = getGroup(id);
        return group.getUser()
                .map(user -> user.getId().equals(userId))
                .orElse(false);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        QuestionGroup group = getGroup(id);
        group.getInterviewQuestions().forEach(InterviewQuestion::delete);
        group.delete();
    }
}
