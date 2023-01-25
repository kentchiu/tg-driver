package com.kent.base.exception;


import org.springframework.http.HttpStatus;

/**
 * (HTTP STATUS 400) 非法的 API 使用: 通常使用在 API 的调用方式或参赛出错
 */
public class IllegalApiUsageException extends ApiException {

    public IllegalApiUsageException() {
        super(HttpStatus.BAD_REQUEST);
    }

    public IllegalApiUsageException(String reason) {
        super(HttpStatus.BAD_REQUEST, reason);
    }

    public IllegalApiUsageException(String reason, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, reason, cause);
    }
}
