package com.greenroom.modulecommon.controller;

import com.greenroom.modulecommon.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartException;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private <T> ResponseEntity<ApiError<T>> createResponse(T exception, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(ApiError.from(exception), headers, status);
    }

    /**
     * AuthenticationException 인증 과정중에 일어난 에러도 400으로 처리
     */
    @ExceptionHandler({
            IllegalStateException.class, IllegalArgumentException.class,
            TypeMismatchException.class, HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class, MultipartException.class,
            AuthenticationException.class, DateTimeParseException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<?> handleBadRequestException(Exception e) {
        String message = e.getMessage();
        return createResponse(message, HttpStatus.BAD_REQUEST);
    }

    /**
     * ApiController 내 @Valid 전용 예외
     *
     * invalid_fields 예시
     *
     * {
     *   "name" : "비어 있을 수 없습니다",
     *   "email" : "비어 있을 수 없습니다"
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidException(MethodArgumentNotValidException e) {
        Map<String, String> errorMap = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> errorMap.put(fieldError.getField(), fieldError.getDefaultMessage()));

        String errorMessage = "Bad request exception occurred";
        return createResponse(errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * handle kakao login api exception
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<?> handleHttpClientErrorException(HttpClientErrorException e) {
        HttpStatus httpStatus = HttpStatus.valueOf(e.getRawStatusCode());
        String message = e.getMessage();
        return createResponse(message, httpStatus);
    }

    /**
     * api 모듈에서 정의한 비즈니스 예외가 발생하는 경우
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException e) {
        String message = e.getMessage();
        return createResponse(message, e.getType().getStatus());
    }

    /**
     * 예상하지 못한 API Server Exception Handling
     */
    @ExceptionHandler({RuntimeException.class, Exception.class})
    public ResponseEntity<?> handleException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);
        String clientMessage = "알 수 없는 에러가 발생했습니다. 서버 관리자에게 문의하세요.";
        return createResponse(clientMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
