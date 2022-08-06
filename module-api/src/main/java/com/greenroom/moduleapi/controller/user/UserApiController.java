package com.greenroom.moduleapi.controller.user;

import com.greenroom.moduleapi.security.oauth.KakaoOAuthDto;
import com.greenroom.moduleapi.security.oauth.KakaoOAuthService;
import com.greenroom.moduleapi.security.oauth.NaverOAuthDto;
import com.greenroom.moduleapi.security.oauth.NaverOAuthService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.controller.ApiResult;
import com.greenroom.modulecommon.entity.user.OAuthType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final UserService userService;

    @PostMapping("/join")
    public ApiResult<Long> join(@RequestBody UserRequest.JoinRequest request) {
        String accessToken = request.getAccessToken();
        OAuthType oAuthType = OAuthType.from(request.getOauthType());

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

        return ApiResult.OK(userService.create(oauthId, oAuthType));
    }
}
