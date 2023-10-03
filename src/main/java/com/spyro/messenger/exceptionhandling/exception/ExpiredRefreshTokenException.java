package com.spyro.messenger.exceptionhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class ExpiredRefreshTokenException extends BaseException{
    public ExpiredRefreshTokenException(HttpStatus httpStatus, String description) {
        super(httpStatus, description);
    }
}
