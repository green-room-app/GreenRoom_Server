package com.greenroom.moduleapi.security.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.greenroom.moduleapi.config.AppleOAuthConfig;
import com.greenroom.modulecommon.exception.ApiException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;

import static com.greenroom.modulecommon.exception.EnumApiException.UNAUTHORIZED;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleOAuthService {

    private final RestTemplate restTemplate;
    private final AppleOAuthConfig oAuthConfig;

    public AppleOAuthDto.LoginResponse getUserInfo(AppleOAuthDto.LoginRequest request) {
        DecodedJWT decodedJWT = JWT.decode(request.getIdentityToken());
        String kid = decodedJWT.getKeyId();
        String alg = decodedJWT.getAlgorithm();

        if (isEmpty(kid) || isEmpty(alg)) {
            throw new ApiException(UNAUTHORIZED, "keyId 또는 algorithm 값이 없습니다");
        }

        // appleToken 검증용 public key 요청
        PublicKey publicKey = getPublicKey(kid, alg);

        Claims userInfo = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(request.getIdentityToken()).getBody();

        if (!verifyToken(userInfo)) {
            throw new ApiException(UNAUTHORIZED, "올바른 토큰이 아닙니다");
        }

        String userId = userInfo.get("sub", String.class);
        return AppleOAuthDto.LoginResponse.from(userId);
    }

    private boolean verifyToken(Claims claims) {
        String issuer = claims.get("iss", String.class);
        String aud = claims.get("aud", String.class);
        String userId = claims.get("sub", String.class);
        Date expiredTime = claims.get("exp", Date.class);
        Date currentTime = new Date(System.currentTimeMillis());

        return oAuthConfig.getIssuer().equals(issuer) &&
                oAuthConfig.getAppBundleId().equals(aud) &&
                isNotEmpty(userId) &&
                currentTime.before(expiredTime);
    }

    private PublicKey getPublicKey(String keyId, String algorithm) {
        AppleOAuthDto.AuthKeyResponse publicKeys = getPublicKeys();

        AppleOAuthDto.PublicKeyResponse targetPublicKey = publicKeys.getKeys()
            .stream()
            .filter(key -> key.getKid().equals(keyId) && key.getAlg().equals(algorithm))
            .findFirst().orElseThrow(() -> new ApiException(UNAUTHORIZED, "발급받은 공개키 3개 중에 매칭되는 키가 없습니다"));

        byte[] nBytes = Base64.getUrlDecoder().decode(targetPublicKey.getN());
        byte[] eBytes = Base64.getUrlDecoder().decode(targetPublicKey.getE());

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            KeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            return publicKey;

        } catch (Exception exception) {
            log.warn("공개키를 생성하는 중 문제가 발생했습니다", exception);
            throw new ApiException(UNAUTHORIZED, "사용자 검증을 위한 공개키를 생성하는 중 문제가 발생했습니다");
        }
    }

    private AppleOAuthDto.AuthKeyResponse getPublicKeys() {
        String requestUrl = oAuthConfig.getAuthKeyUrl();
        ResponseEntity<AppleOAuthDto.AuthKeyResponse> response
            = restTemplate.getForEntity(requestUrl, AppleOAuthDto.AuthKeyResponse.class);

        return response.getBody();
    }
}
