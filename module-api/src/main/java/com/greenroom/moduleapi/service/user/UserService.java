package com.greenroom.moduleapi.service.user;

import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;

public interface UserService {
    User getUser(Long id);

    User getUserByOauthIdAndOauthType(String oauthId, OAuthType oauthType);
}
