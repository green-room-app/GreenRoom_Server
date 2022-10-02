package com.greenroom.moduleapi.controller.auth;

import com.greenroom.moduleapi.security.jwt.AuthService;
import com.greenroom.moduleapi.security.jwt.RefreshTokenService;
import com.greenroom.moduleapi.security.oauth.*;
import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import com.greenroom.modulecommon.jwt.JwtAuthenticationToken;
import com.greenroom.modulecommon.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.greenroom.modulecommon.exception.EnumApiException.FORBIDDEN;

@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthApiController {

    private final AuthService authService;
    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public AuthDto.AuthResponse login(@Valid @RequestBody AuthDto.LoginRequest loginRequest) {
        String accessToken = loginRequest.getAccessToken();
        OAuthType oAuthType = OAuthType.from(loginRequest.getOauthType());

        String oauthId;

        switch (oAuthType) {
            case KAKAO:
                oauthId = kakaoOAuthService.getUserInfo(KakaoOAuthDto.LoginRequest.from(accessToken)).getId();
                break;
            case NAVER:
                oauthId = naverOAuthService.getUserInfo(NaverOAuthDto.LoginRequest.from(accessToken)).getId();
                break;
            default:
                oauthId = appleOAuthService.getUserInfo(AppleOAuthDto.LoginRequest.from(accessToken)).getId();
        }

        JwtAuthenticationToken authToken = JwtAuthenticationToken.from(oauthId, oAuthType);
        Authentication authenticate = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        AuthDto.AuthResponse response = (AuthDto.AuthResponse)authenticate.getDetails();

        return response;
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal JwtAuthentication authentication) {
        refreshTokenService.deleteRefreshToken(authentication.getId());
        SecurityContextHolder.clearContext();
    }

    @PostMapping("/reissue")
    public AuthDto.ReissueResponse reissue(@Valid @RequestBody AuthDto.ReissueRequest reissueRequest) {

        if (!jwtProvider.verifyToken(reissueRequest.getRefreshToken())) {
            throw new ApiException(FORBIDDEN, "Invalid refreshToken");
        }

        Long id = authService.getId(reissueRequest.getAccessToken());

        String refreshToken = refreshTokenService.getRefreshToken(id);

        if (!refreshToken.equals(reissueRequest.getRefreshToken())) {
            throw new ApiException(FORBIDDEN, "Mismatched refreshToken");
        }

        String renewedAccessToken = jwtProvider.createRenewedAccessToken(reissueRequest.getAccessToken());
        String newRefreshToken = jwtProvider.createRefreshToken();

        refreshTokenService.saveRefreshToken(id, newRefreshToken);
        SecurityContextHolder.clearContext();

        AuthDto.ReissueResponse reissueResponse = AuthDto.ReissueResponse.builder()
                                                        .accessToken(renewedAccessToken)
                                                        .expiresIn(jwtProvider.getExpirySeconds())
                                                        .refreshToken(newRefreshToken)
                                                        .refreshTokenExpiresIn(jwtProvider.getRefreshExpirySeconds())
                                                        .build();

        return reissueResponse;
    }
}
