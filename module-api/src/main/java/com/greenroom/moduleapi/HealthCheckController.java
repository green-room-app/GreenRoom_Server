package com.greenroom.moduleapi;

import com.greenroom.modulecommon.controller.ApiResult;
import com.greenroom.modulecommon.jwt.JwtAuthentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/health")
@RestController
public class HealthCheckController {

    @GetMapping
    public ApiResult<Long> getSystemTimeMillis() {
        return ApiResult.OK(System.currentTimeMillis());
    }

    @GetMapping("/with-user")
    public ApiResult<Long> getSystemTimeMillisWithUser(@AuthenticationPrincipal JwtAuthentication authentication) {
        return ApiResult.OK(System.currentTimeMillis());
    }
}
