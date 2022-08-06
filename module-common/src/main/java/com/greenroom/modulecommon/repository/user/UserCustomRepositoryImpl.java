package com.greenroom.modulecommon.repository.user;

import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.QUser;
import com.greenroom.modulecommon.entity.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import static com.greenroom.modulecommon.entity.user.QUser.user;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean exists(String name) {
        User result = jpaQueryFactory
                        .selectFrom(user)
                        .where(user.name.eq(name))
                        .fetchFirst();

        return result != null;
    }

    @Override
    public boolean existsByOAuthIdAndType(String oauthId, OAuthType oauthType) {
        User result = jpaQueryFactory
                        .selectFrom(user)
                        .where(
                            user.oauthId.eq(oauthId),
                            user.oauthType.eq(oauthType)
                        )
                        .fetchFirst();

        return result != null;
    }
}
