package com.spyro.messenger.exceptionhandling.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class BaseException extends RuntimeException {
    protected HttpStatus httpStatus;
    protected String description;

    public BaseException(HttpStatus httpStatus, String description) {
        super(description);
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
