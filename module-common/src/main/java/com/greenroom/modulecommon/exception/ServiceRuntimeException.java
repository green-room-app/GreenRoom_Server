package com.greenroom.modulecommon.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ServiceRuntimeException extends RuntimeException {

    // 필요 시 에러 메시지에 추가할 parameter
    private final Object[] params;
}
