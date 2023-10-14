package com.spyro.messenger.exceptionhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnableToRecoverAccountException extends BaseException {
    public UnableToRecoverAccountException(HttpStatus httpStatus, String description) {
        super(httpStatus, description);
    }
}