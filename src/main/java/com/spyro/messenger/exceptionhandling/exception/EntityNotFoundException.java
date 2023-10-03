package com.spyro.messenger.exceptionhandling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(Class<?> clazz) {
        httpStatus = HttpStatus.NOT_FOUND;
        description = clazz.getSimpleName() + " entity doesn't found in database";
    }
}
