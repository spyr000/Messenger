package com.spyro.messenger.exceptionhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidRefreshTokenException extends BaseException {
    public InvalidRefreshTokenException(HttpStatus httpStatus, String description) {
        super(httpStatus, description);
    }
}
