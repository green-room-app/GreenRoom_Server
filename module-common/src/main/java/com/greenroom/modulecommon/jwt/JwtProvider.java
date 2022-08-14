package com.greenroom.modulecommon.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.greenroom.modulecommon.entity.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Date;

/**
 * JWT를 발행하는 클래스
 */
@Getter
public class JwtProvider {

    private final String issuer;

    private final String clientSecret;

    private final int expirySeconds;

    private final int refreshExpirySeconds;

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier;

    public JwtProvider(String issuer, String clientSecret, int expirySeconds, int refreshExpirySeconds) {
        this.issuer = issuer;
        this.clientSecret = clientSecret;
        this.expirySeconds = expirySeconds;
        this.refreshExpirySeconds = refreshExpirySeconds;
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    public String createAccessToken(User user, String[] roles) {
        Claims claims = Claims.of(user, roles);
        return createTokenByClaims(claims);
    }

    public String createRenewedAccessToken(String accessToken) throws JWTVerificationException {
        Claims claims = verify(accessToken);
        claims.eraseIat();
        claims.eraseExp();
        return createTokenByClaims(claims);
    }

    public String createRefreshToken() {
        Date now = new Date();
        JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
        builder.withIssuer(issuer);
        builder.withIssuedAt(now);

        if (refreshExpirySeconds > 0) {
            builder.withExpiresAt(new Date(now.getTime() + (long)refreshExpirySeconds * 1_000L));
        }

        return builder.sign(algorithm);
    }

    public String createTokenByClaims(Claims claims) {
        Date now = new Date();
        JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
        builder.withIssuer(issuer);
        builder.withIssuedAt(now);

        if (expirySeconds > 0) {
            builder.withExpiresAt(new Date(now.getTime() + (long)expirySeconds * 1_000L));
        }

        builder.withClaim("id", claims.id);
        builder.withClaim("oauthType", claims.oauthType);
        builder.withClaim("oauthId", claims.oauthId);
        builder.withArrayClaim("roles", claims.roles);
        return builder.sign(algorithm);
    }

    public boolean verifyToken(String token) {
        try {
            verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public Claims verify(String token) throws JWTVerificationException {
        return new Claims(jwtVerifier.verify(token));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Claims {
        private Long id;
        private int oauthType;
        private String oauthId;
        private String[] roles;
        private Date iat;
        private Date exp;

        private Claims(DecodedJWT decodedJWT) {
            Claim id = decodedJWT.getClaim("id");
            if (!id.isNull())
                this.id = id.asLong();

            Claim oauthType = decodedJWT.getClaim("oauthType");
            if (!oauthType.isNull())
                this.oauthType = oauthType.asInt();

            Claim oauthId = decodedJWT.getClaim("oauthId");
            if (!oauthId.isNull())
                this.oauthId = oauthId.asString();

            Claim roles = decodedJWT.getClaim("roles");
            if (!roles.isNull())
                this.roles = roles.asArray(String.class);

            this.iat = decodedJWT.getIssuedAt();
            this.exp = decodedJWT.getExpiresAt();
        }

        public static Claims of(User user, String[] roles) {
            Claims claims = new Claims();
            claims.id = user.getId();
            claims.oauthType = user.getOauthType().getCode();
            claims.oauthId = user.getOauthId();
            claims.roles = roles;
            return claims;
        }

        public static Claims of(Long id, int oauthType, String oauthId, String[] roles) {
            Claims claims = new Claims();
            claims.id = id;
            claims.oauthType = oauthType;
            claims.oauthId = oauthId;
            claims.roles = roles;
            return claims;
        }

        public long iat() {
            return ObjectUtils.isNotEmpty(iat) ? iat.getTime() : -1;
        }

        public long exp() {
            return ObjectUtils.isNotEmpty(exp) ? exp.getTime() : -1;
        }

        public void eraseIat() {
            iat = null;
        }

        public void eraseExp() {
            exp = null;
        }
    }
}
