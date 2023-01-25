package com.kent.base.exception;


import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

/**
 * (HTTP STATUS 404) 找不到资源的异常
 */
public class ResourceNotFoundException extends ApiException {

    private String message;

    public ResourceNotFoundException() {
        super(HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String reason) {
        super(HttpStatus.NOT_FOUND, reason);
    }

    public ResourceNotFoundException(String reason, Throwable cause) {
        super(HttpStatus.NOT_FOUND, reason, cause);
    }

    public ResourceNotFoundException(Class target, String id) {
        super(HttpStatus.NOT_FOUND);
        message = String.format("resource %s@%s not found", target.getSimpleName(), id);
    }

    public ResourceNotFoundException(String resourceName, String id) {
        super(HttpStatus.NOT_FOUND);
        message = String.format("resource %s@%s not found", resourceName, id);
    }

    @Override
    public String getMessage() {
        if (StringUtils.isNotBlank(this.message)) {
            return this.message;
        } else {
            return super.getMessage();
        }
    }

}


