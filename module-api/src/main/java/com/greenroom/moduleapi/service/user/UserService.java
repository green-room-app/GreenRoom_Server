package com.greenroom.moduleapi.service.user;

import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;

public interface UserService {
    Long create(String oauthId, OAuthType oauthType, Long categoryId, String name);

    User getUser(Long id);

    User getUserByOauthIdAndOauthType(String oauthId, OAuthType oauthType);

    void update(Long id, String name);

    void update(Long id, Long categoryId);

    void update(Long id, Long categoryId, String name);

    boolean isUniqueName(String name);
}
