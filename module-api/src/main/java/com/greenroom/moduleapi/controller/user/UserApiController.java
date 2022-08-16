package com.greenroom.moduleapi.controller.user;

import com.greenroom.moduleapi.controller.user.UserRequest.JoinRequest;
import com.greenroom.moduleapi.controller.user.UserResponse.JoinResponse;
import com.greenroom.moduleapi.security.oauth.KakaoOAuthDto;
import com.greenroom.moduleapi.security.oauth.KakaoOAuthService;
import com.greenroom.moduleapi.security.oauth.NaverOAuthDto;
import com.greenroom.moduleapi.security.oauth.NaverOAuthService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final UserService userService;

    /**
     * 회원가입 API
     */
    @PostMapping("/join")
    public JoinResponse join(@Valid @RequestBody JoinRequest request) {
        String accessToken = request.getAccessToken();
        OAuthType oAuthType = OAuthType.from(request.getOauthType());

        String oauthId;

        switch (oAuthType) {
            case KAKAO:
                oauthId = kakaoOAuthService.getUserInfo(KakaoOAuthDto.LoginRequest.from(accessToken)).getId();
                break;
            case NAVER:
                oauthId = naverOAuthService.getUserInfo(NaverOAuthDto.LoginRequest.from(accessToken)).getId();
                break;
            default:
                oauthId = "";
        }

        return JoinResponse.from(userService.create(oauthId, oAuthType, request.getCategoryId(), request.getName()));
    }

    /**
     * 회원정보 수정 API
     */
    @PutMapping
    public void update(@AuthenticationPrincipal JwtAuthentication authentication,
                       @RequestBody UserRequest.UpdateRequest request) {

        if (request.getCategoryId() == null && isEmpty(request.getName())) {
            throw new IllegalArgumentException("name과 categoryId 필드 중 적어도 하나는 있어야 합니다.");
        }

        if (request.getCategoryId() == null) {
            userService.update(authentication.getId(), request.getName());
            return;
        }

        if (isEmpty(request.getName())) {
            userService.update(authentication.getId(), request.getCategoryId());
            return;
        }

        userService.update(authentication.getId(), request.getCategoryId(), request.getName());
    }

    /**
     * 닉네임 중복 API
     */
    @GetMapping("/name")
    public boolean isValidNickname(@RequestParam("name") String name) {
        return userService.isUniqueName(name);
    }
}
