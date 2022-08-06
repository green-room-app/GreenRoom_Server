package com.greenroom.modulecommon.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Getter
@AllArgsConstructor
public enum OAuthType {
    KAKAO(0,"kakao"),
    NAVER(1,"naver"),
    APPLE(2,"apple");

    private static final Map<Integer, OAuthType> oAuthTypeMap =
            Stream.of(values()).collect(toMap(OAuthType::getCode, value -> value));

    private Integer code;
    private String value;

    public static OAuthType from(int oAuthTypeCode) {
        OAuthType oAuthType = oAuthTypeMap.get(oAuthTypeCode);
        if (oAuthType == null) {
            throw new IllegalArgumentException("잘못된 OAuthType 타입입니다. 0~2 중 하나를 입력해야 합니다.");
        }

        return oAuthType;
    }
}
