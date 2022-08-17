package com.greenroom.moduleapi.service.user;

import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;

public interface UserService {
    Long create(String oauthId, OAuthType oauthType, Long categoryId, String name);

    User getUser(Long id);

    User getUserByOauthIdAndOauthType(String oauthId, OAuthType oauthType);

    Long update(Long id, String name);

    Long update(Long id, Long categoryId);

    Long update(Long id, Long categoryId, String name);

    Long updateProfileImage(Long id, String profileImage);

    boolean isUniqueName(String name);
}
