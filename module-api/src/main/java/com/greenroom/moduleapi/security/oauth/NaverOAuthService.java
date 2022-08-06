package com.greenroom.moduleapi.security.oauth;

import com.greenroom.moduleapi.config.NaverOAuthConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverOAuthService  {

    private final RestTemplate restTemplate;
    private final NaverOAuthConfig oAuthConfig;

    public NaverOAuthDto.LoginResponse getUserInfo(NaverOAuthDto.LoginRequest request) {
        HttpEntity authRequest = createLoginRequest(request);
        String requestUrl = oAuthConfig.getLoginUrl();

        ResponseEntity<NaverOAuthDto.LoginResponse> oauthResponse
                = restTemplate.exchange(requestUrl, HttpMethod.GET, authRequest, NaverOAuthDto.LoginResponse.class);

        return oauthResponse.getBody();
    }

    public NaverOAuthDto.LogoutResponse logout(NaverOAuthDto.LogoutRequest request) {
        return null;
    }

    /**
     * HttpHeader key: Authorization
     * HttpHeader value: Bearer ${ACCESS_TOKEN}
     *
     * @return Authorization: Bearer ${ACCESS_TOKEN}
     */
    private HttpEntity createLoginRequest(NaverOAuthDto.LoginRequest loginRequest) {
        String headerValue = createRequestHeader(loginRequest.getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.set(oAuthConfig.getHeaderKey(), headerValue);

        return new HttpEntity(headers);
    }

    /**
     * @return Bearer ${ACCESS_TOKEN}
     */
    private String createRequestHeader(String accessToken) {
        return String.format("%s %s", oAuthConfig.getHeaderValue(), accessToken);
    }
}
