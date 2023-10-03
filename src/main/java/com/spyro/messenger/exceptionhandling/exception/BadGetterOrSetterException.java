package com.spyro.messenger.exceptionhandling.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BadGetterOrSetterException extends BaseException {
    public BadGetterOrSetterException(HttpStatus httpStatus, String description) {
        super(httpStatus, description);
    }
}
