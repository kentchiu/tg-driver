package com.kent.base.exception;

import org.springframework.http.HttpStatus;

/**
 * (HTTP STATUS 409) 违反唯一值时的异常, 通常使用在 no, name 是唯一值, 但是出现了重复
 */
public class DuplicateException extends ApiException {

    public DuplicateException(HttpStatus status) {
        super(HttpStatus.CONFLICT);
    }

    public DuplicateException(String reason) {
        super(HttpStatus.CONFLICT, reason);
    }

    public DuplicateException(String reason, Throwable cause) {
        super(HttpStatus.CONFLICT, reason, cause);
    }
}
