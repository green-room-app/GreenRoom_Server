package com.greenroom.modulecommon.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EnumApiException {
    NOT_FOUND("error.notfound", "error.notfound.details", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("error.authentication","error.authentication.details", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("error.authority","error.authority.details", HttpStatus.FORBIDDEN);

    private String messageKey;
    private String messageDetailKey;
    private HttpStatus status;
}
