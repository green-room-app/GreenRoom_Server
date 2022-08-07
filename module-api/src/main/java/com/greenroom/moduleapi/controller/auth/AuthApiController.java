package com.greenroom.moduleapi.controller.auth;

import com.greenroom.moduleapi.security.jwt.RefreshTokenService;
import com.greenroom.moduleapi.security.oauth.KakaoOAuthDto;
import com.greenroom.moduleapi.security.oauth.KakaoOAuthService;
import com.greenroom.moduleapi.security.oauth.NaverOAuthDto;
import com.greenroom.moduleapi.security.oauth.NaverOAuthService;
import com.greenroom.modulecommon.controller.ApiResult;
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

import static com.greenroom.modulecommon.controller.ApiResult.OK;
import static com.greenroom.modulecommon.exception.EnumApiException.UNAUTHORIZED;

@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthApiController {

    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final RefreshTokenService refreshTokenService;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ApiResult<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest loginRequest) {

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
                //FIXME: Apple Login 로직 완성 필요
                oauthId = "";
        }

        JwtAuthenticationToken authToken = JwtAuthenticationToken.from(oauthId, oAuthType);
        Authentication authenticate = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        AuthDto.AuthResponse response = (AuthDto.AuthResponse)authenticate.getDetails();

        return OK(response);
    }

    @PostMapping("/logout")
    public ApiResult<Void> logout(@AuthenticationPrincipal JwtAuthentication authentication,
                                  @Valid @RequestBody AuthDto.LogoutRequest logoutRequest) {

        String accessToken = logoutRequest.getAccessToken();
        OAuthType oAuthType = OAuthType.from(logoutRequest.getOauthType());

        switch (oAuthType) {
            case KAKAO:
                kakaoOAuthService.logout(KakaoOAuthDto.LogoutRequest.from(accessToken)).getId();
                break;
            case NAVER:
                naverOAuthService.logout(NaverOAuthDto.LogoutRequest.from(accessToken)).getId();
                break;
            default:
                //FIXME: Apple Logout 로직 완성 필요
                break;
        }

        refreshTokenService.deleteRefreshToken(authentication.getId());
        SecurityContextHolder.clearContext();

        return OK();
    }

    @PostMapping("/reissue")
    public ApiResult<AuthDto.ReissueResponse> reissue(@AuthenticationPrincipal JwtAuthentication authentication,
                                                      @Valid @RequestBody AuthDto.ReissueRequest reissueRequest) {

        if (!jwtProvider.verifyToken(reissueRequest.getRefreshToken())) {
            throw new ApiException(UNAUTHORIZED, "Invalid refreshToken");
        }

        String refreshToken = refreshTokenService.getRefreshToken(authentication.getId());

        if (!refreshToken.equals(reissueRequest.getRefreshToken())) {
            throw new ApiException(UNAUTHORIZED, "Mismatched refreshToken");
        }

        String renewedApiToken = jwtProvider.createRenewedApiToken(reissueRequest.getAccessToken());
        String newRefreshToken = jwtProvider.createRefreshToken();

        refreshTokenService.saveRefreshToken(authentication.getId(), newRefreshToken);
        SecurityContextHolder.clearContext();

        return OK(AuthDto.ReissueResponse.of(renewedApiToken, newRefreshToken));
    }
}
