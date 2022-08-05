package com.greenroom.moduleapi.controller.auth;

import com.greenroom.moduleapi.security.jwt.RefreshTokenService;
import com.greenroom.moduleapi.security.oauth.KakaoOAuthDto;
import com.greenroom.moduleapi.security.oauth.KakaoOAuthService;
import com.greenroom.moduleapi.security.oauth.NaverOAuthDto;
import com.greenroom.moduleapi.security.oauth.NaverOAuthService;
import com.greenroom.modulecommon.entity.user.OAuthType;
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

@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthApiController {

    private final NaverOAuthService naverOAuthService;
    private final KakaoOAuthService kakaoOAuthService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public AuthDto.AuthResponse login(@Valid @RequestBody AuthDto.LoginRequest loginRequest) {

        String accessToken = loginRequest.getAccessToken();
        OAuthType oAuthType = OAuthType.from(loginRequest.getOauthType());

        String oauthId;

        switch (oAuthType) {
            case NAVER:
                oauthId = naverOAuthService.getUserInfo(NaverOAuthDto.LoginRequest.from(accessToken)).getId();
                break;
            case KAKAO:
                oauthId = kakaoOAuthService.getUserInfo(KakaoOAuthDto.LoginRequest.from(accessToken)).getId();
                break;
            default:
                oauthId = "";
        }

        JwtAuthenticationToken authToken = JwtAuthenticationToken.from(oauthId, oAuthType);
        Authentication authenticate = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        AuthDto.AuthResponse response = (AuthDto.AuthResponse)authenticate.getDetails();
        return response;
    }

    //FIXME: RuntimeException 제거
    @PostMapping("/reissue")
    public AuthDto.ReissueResponse reissue(@AuthenticationPrincipal JwtAuthentication authentication,
                                           @Valid @RequestBody AuthDto.ReissueRequest reissueRequest) {

        if (!jwtProvider.verifyToken(reissueRequest.getRefreshToken())) {
            throw new RuntimeException();
        }

        String refreshToken = refreshTokenService.getRefreshToken(authentication.getId());

        if (!refreshToken.equals(reissueRequest.getRefreshToken())) {
            throw new RuntimeException();
        }

        String renewedApiToken = jwtProvider.createRenewedApiToken(reissueRequest.getAccessToken());
        String newRefreshToken = jwtProvider.createRefreshToken();

        refreshTokenService.saveRefreshToken(authentication.getId(), newRefreshToken);
        SecurityContextHolder.clearContext();

        return AuthDto.ReissueResponse.of(renewedApiToken, newRefreshToken);
    }
}
