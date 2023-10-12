package com.spyro.messenger.exceptionhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnableToChangePasswordException extends BaseException {
    public UnableToChangePasswordException(HttpStatus httpStatus, String description) {
        super(httpStatus, description);
    }
}