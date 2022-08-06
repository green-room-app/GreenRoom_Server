package com.greenroom.modulecommon.repository.user;

import com.greenroom.modulecommon.entity.user.OAuthType;

public interface UserCustomRepository {
    boolean exists(String name);
    boolean existsByOAuthIdAndType(String oauthId, OAuthType oauthType);
}
