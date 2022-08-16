package com.greenroom.modulecommon.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiError<T> {

    private final T message;   // 오류 메시지

    public static <T> ApiError from(T exceptionMessage) {
        return new ApiError<>(exceptionMessage);
    }
}
