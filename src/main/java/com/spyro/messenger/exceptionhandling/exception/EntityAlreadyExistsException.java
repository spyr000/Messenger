package com.spyro.messenger.exceptionhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityAlreadyExistsException extends BaseException {
    public EntityAlreadyExistsException(Class<?> clazz) {
        httpStatus = HttpStatus.BAD_REQUEST;
        description = clazz.getSimpleName() + " entity already exists";
    }
}
