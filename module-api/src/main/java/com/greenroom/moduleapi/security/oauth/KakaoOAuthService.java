package com.greenroom.moduleapi.security.oauth;

import com.greenroom.moduleapi.config.KakaoOAuthConfig;
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
public class KakaoOAuthService {

    private final RestTemplate restTemplate;
    private final KakaoOAuthConfig oAuthConfig;

    public KakaoOAuthDto.LoginResponse getUserInfo(KakaoOAuthDto.LoginRequest request) {
        HttpEntity authRequest = createLoginRequest(request);
        String requestUrl = oAuthConfig.getLoginUrl();

        ResponseEntity<KakaoOAuthDto.LoginResponse> oauthResponse
                = restTemplate.exchange(requestUrl, HttpMethod.GET, authRequest, KakaoOAuthDto.LoginResponse.class);

        return oauthResponse.getBody();
    }

    public KakaoOAuthDto.LogoutResponse logout(KakaoOAuthDto.LogoutRequest request) {
        HttpEntity logoutRequest = createLogoutRequest(request);
        String requestUrl = oAuthConfig.getLogoutUrl();

        ResponseEntity<KakaoOAuthDto.LogoutResponse> oauthResponse
                = restTemplate.exchange(requestUrl, HttpMethod.POST, logoutRequest, KakaoOAuthDto.LogoutResponse.class);

        return oauthResponse.getBody();
    }

    /**
     * HttpHeader key: Authorization
     * HttpHeader value: Bearer ${ACCESS_TOKEN}
     *
     * @return Authorization: Bearer ${ACCESS_TOKEN}
     */
    private HttpEntity createLoginRequest(KakaoOAuthDto.LoginRequest loginRequest) {
        String headerValue = createRequestHeader(loginRequest.getAccessToken());

        HttpHeaders headers = new HttpHeaders();
        headers.set(oAuthConfig.getHeaderKey(), headerValue);

        return new HttpEntity(headers);
    }

    /**
     * HttpHeader key: Authorization
     * HttpHeader value: Bearer ${ACCESS_TOKEN}
     *
     * @return Authorization: Bearer ${ACCESS_TOKEN}
     */
    private HttpEntity createLogoutRequest(KakaoOAuthDto.LogoutRequest logoutRequest) {
        String headerValue = createRequestHeader(logoutRequest.getAccessToken());

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
