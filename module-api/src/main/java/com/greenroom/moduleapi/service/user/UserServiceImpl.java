package com.greenroom.moduleapi.service.user;

import com.greenroom.moduleapi.service.category.CategoryService;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public Long create(String oauthId, OAuthType oauthType) {
        checkArgument(oauthId != null, "oauthId 값은 필수입니다.");
        checkArgument(oauthType != null, "oauthType 값은 필수입니다.");

        if (userRepository.existsByOAuthIdAndType(oauthId, oauthType)) {
            return getUserByOauthIdAndOauthType(oauthId, oauthType).getId();
        }

        User user = User.builder()
                        .oauthId(oauthId)
                        .oauthType(oauthType)
                        .build();

        userRepository.save(user);

        return user.getId();
    }

    @Override
    public User getUser(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException(NOT_FOUND, User.class, String.format("id = %s", id)));
    }

    @Override
    public User getUserByOauthIdAndOauthType(String oauthId, OAuthType oauthType) {
        checkArgument(oauthId != null, "oauthId 값은 필수입니다.");
        checkArgument(oauthType != null, "oauthType 값은 필수입니다.");

        return userRepository.findByOauthIdAndOauthType(oauthId, oauthType)
                .orElseThrow(() -> new ApiException(NOT_FOUND,
                        User.class,
                        String.format("oauthId = %s, oauthType = %s", oauthId, oauthType.getValue())));
    }

    @Override
    @Transactional
    public void update(Long id, String name) {
        checkArgument(name != null, "name 값은 필수입니다.");
        checkArgument(name.length() <= 20, "name은 20자 이하여야 합니다.");

        User user = getUser(id);
        user.update(name);
    }

    @Override
    @Transactional
    public void update(Long id, Long categoryId) {
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");

        User user = getUser(id);
        Category category = categoryService.getCategory(categoryId);

        user.update(category);
    }

    @Override
    @Transactional
    public void update(Long id, Long categoryId, String name) {
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");
        checkArgument(name != null, "name 값은 필수입니다.");
        checkArgument(name.length() <= 20, "name은 20자 이하여야 합니다.");

        User user = getUser(id);
        Category category = categoryService.getCategory(categoryId);

        user.update(category, name);
    }

    @Override
    public boolean isUniqueName(String name) {
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= 20, "name 길이는 20자 이하만 가능합니다.");

        return !userRepository.exists(name);
    }
}
