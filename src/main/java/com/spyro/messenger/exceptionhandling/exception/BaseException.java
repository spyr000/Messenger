package com.spyro.messenger.exceptionhandling.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseException extends RuntimeException {
    protected HttpStatus httpStatus;
    protected String description;
}
