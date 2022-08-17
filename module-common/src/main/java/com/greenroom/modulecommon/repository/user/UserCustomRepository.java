package com.greenroom.modulecommon.repository.user;

import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;

import java.util.Optional;

public interface UserCustomRepository {
    boolean exists(String name);
    boolean existsByOAuthIdAndType(String oauthId, OAuthType oauthType);
    Optional<User> find(Long id);
    Optional<User> findByOauthIdAndOauthType(String oauthId, OAuthType oAuthType);
}
