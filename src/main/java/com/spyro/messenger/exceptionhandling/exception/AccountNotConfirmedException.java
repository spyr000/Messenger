package com.spyro.messenger.exceptionhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccountNotConfirmedException extends BaseException {
    public AccountNotConfirmedException(HttpStatus httpStatus, String description) {
        super(httpStatus, description);
    }
}