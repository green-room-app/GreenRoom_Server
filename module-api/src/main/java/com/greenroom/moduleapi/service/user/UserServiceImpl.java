package com.greenroom.moduleapi.service.user;

import com.greenroom.moduleapi.service.category.CategoryService;
import com.greenroom.modulecommon.entity.category.Category;
import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.user.UserRepository;
import com.greenroom.modulecommon.util.UploadUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.User.NAME_LENGTH;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final UploadUtils uploadUtils;

    @Override
    @Transactional
    public Long create(String oauthId, OAuthType oauthType, Long categoryId, String name) {
        checkArgument(oauthId != null, "oauthId 값은 필수입니다.");
        checkArgument(oauthType != null, "oauthType 값은 필수입니다.");
        checkArgument(categoryId != null, "categoryId 값은 필수입니다.");
        checkArgument(name != null, "name 값은 필수입니다.");

        if (userRepository.existsByOAuthIdAndType(oauthId, oauthType)) {
            return getUserByOauthIdAndOauthType(oauthId, oauthType).getId();
        }

        Category category = categoryService.getCategory(categoryId);

        if (!isUniqueName(name)) {
            throw new IllegalArgumentException("중복된 닉네임입니다.");
        }

        User user = User.builder()
                        .oauthId(oauthId)
                        .oauthType(oauthType)
                        .category(category)
                        .name(name)
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
        checkArgument(name.length() <= NAME_LENGTH, String.format("name 값은 %s자 이하여야 합니다.", NAME_LENGTH));

        User user = getUser(id);
        checkDuplicateName(user, name);
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
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH, String.format("name 값은 %s자 이하여야 합니다.", NAME_LENGTH));

        User user = getUser(id);
        checkDuplicateName(user, name);
        Category category = categoryService.getCategory(categoryId);

        user.update(category, name);
    }

    @Override
    @Transactional
    public void updateProfileImage(Long id, String profileImage) {
        checkArgument(id != null, "id 값은 필수입니다.");
        checkArgument(isNotEmpty(profileImage), "profileImage 값은 필수입니다.");

        if (uploadUtils.isNotImageFile(profileImage)) {
            throw new IllegalArgumentException("png, jpeg, jpg에 해당하는 파일만 업로드할 수 있습니다.");
        }

        String profileImageUrl = toProfileImageUrl(id, profileImage);
        User user = getUser(id);

        user.updateProfileImage(profileImageUrl);
    }

    private String toProfileImageUrl(Long id, String profileImage) {
        String extension = FilenameUtils.getExtension(profileImage.toLowerCase());
        String profileImageUrl = id + "/" + "profile_image" + "." + extension;
        return profileImageUrl;
    }

    private void checkDuplicateName(User user, String name) {
        if (isUniqueName(name)) {
            return;
        }

        if (name.equals(user.getName())) {
            return;
        }

        throw new IllegalArgumentException("중복된 닉네임입니다.");
    }

    @Override
    public boolean isUniqueName(String name) {
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH, String.format("name 값은 %s자 이하여야 합니다.", NAME_LENGTH));

        return !userRepository.exists(name);
    }
}
