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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        String requestUrl = createLogoutRequest(request);

        ResponseEntity<NaverOAuthDto.LogoutResponse> logoutResponse
                = restTemplate.postForEntity(requestUrl, HttpMethod.POST, NaverOAuthDto.LogoutResponse.class);

        return logoutResponse.getBody();
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
     * https://nid.naver.com/oauth2.0/token?
     *  grant_type=delete
     *  &client_id=jyvqXeaVOVmV
     *  &client_secret=527300A0_COq1_XV33cf
     *  &access_token=c8ceMEJisO4Se7uGCEYKK1p52L93bHXLnaoETis9YzjfnorlQwEisqemfpKHUq2gY
     *  &service_provider=NAVER
     */
    private String createLogoutRequest(NaverOAuthDto.LogoutRequest logoutRequest) {
        return String.format("%s" +
                "?" +
                "grant_type=delete" +
                "&client_id=%s" +
                "&client_secret=%s" +
                "&access_token=%s" +
                "&service_provider=NAVER",
                oAuthConfig.getLogoutUrl(),
                oAuthConfig.getClientId(),
                oAuthConfig.getClientSecret(),
                encodeValue(logoutRequest.getAccessToken()));
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("accessToken encoding error", e);
        }
    }

    /**
     * @return Bearer ${ACCESS_TOKEN}
     */
    private String createRequestHeader(String accessToken) {
        return String.format("%s %s", oAuthConfig.getHeaderValue(), accessToken);
    }
}
