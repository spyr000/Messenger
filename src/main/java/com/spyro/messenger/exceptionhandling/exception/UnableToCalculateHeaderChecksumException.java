package com.spyro.messenger.exceptionhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnableToCalculateHeaderChecksumException extends BaseException {
    public UnableToCalculateHeaderChecksumException(HttpStatus httpStatus, String description) {
        super(httpStatus, description);
    }
}