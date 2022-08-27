package com.greenroom.moduleapi.controller.user;

import com.greenroom.moduleapi.controller.user.UserRequest.JoinRequest;
import com.greenroom.moduleapi.controller.user.UserResponse.JoinResponse;
import com.greenroom.moduleapi.controller.user.UserResponse.UpdateProfileImageResponse;
import com.greenroom.moduleapi.security.jwt.RefreshTokenService;
import com.greenroom.moduleapi.security.oauth.*;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.user.OAuthType;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import com.greenroom.modulecommon.util.PresignerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@RequestMapping("/api/users")
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final KakaoOAuthService kakaoOAuthService;
    private final NaverOAuthService naverOAuthService;
    private final AppleOAuthService appleOAuthService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final PresignerUtils presignerUtils;

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
                oauthId = appleOAuthService.getUserInfo(AppleOAuthDto.LoginRequest.from(accessToken)).getId();
        }

        return JoinResponse.from(userService.create(oauthId, oAuthType, request.getCategoryId(), request.getName()));
    }

    @GetMapping
    public UserResponse.GetResponse getUser(@AuthenticationPrincipal JwtAuthentication authentication) {
        User user = userService.getUser(authentication.getId());

        if (isEmpty(user.getProfileImage())) {
            return UserResponse.GetResponse.from(user);
        }

        String profilePresignedUrl = presignerUtils.getPresignedGetUrl(user.getProfileImage());
        return UserResponse.GetResponse.of(user, profilePresignedUrl);
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

    @PutMapping("/profile-image")
    public UpdateProfileImageResponse updateProfileImage(@AuthenticationPrincipal JwtAuthentication authentication,
                                                         @Valid @RequestBody UserRequest.UpdateProfileImageRequest request) {

        Long userId = userService.updateProfileImage(authentication.getId(), request.getProfileImage());
        User user = userService.getUser(userId);
        String profilePresignedUrl = presignerUtils.getPresignedPutUrl(user.getProfileImage());

        return UpdateProfileImageResponse.from(profilePresignedUrl);
    }

    /**
     * 회원정보 삭제 API
     */
    @DeleteMapping
    public void delete(@AuthenticationPrincipal JwtAuthentication authentication) {
        userService.delete(authentication.getId());
        refreshTokenService.deleteRefreshToken(authentication.getId());
        SecurityContextHolder.clearContext();
    }

    /**
     * 닉네임 중복 API
     */
    @GetMapping("/name")
    public boolean isValidNickname(@RequestParam("name") String name) {
        return userService.isUniqueName(name);
    }
}
