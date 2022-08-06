package com.greenroom.moduleapi.service.user;

import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;

public interface UserService {
    Long create(String oauthId, OAuthType oauthType);

    User getUser(Long id);

    User getUserByOauthIdAndOauthType(String oauthId, OAuthType oauthType);

    boolean isUniqueName(String name);
}
