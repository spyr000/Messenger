package com.spyro.messenger.exceptionhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnableToProcessFriendRequestException extends BaseException {
    public UnableToProcessFriendRequestException(HttpStatus httpStatus, String description) {
        super(httpStatus, description);
    }
}