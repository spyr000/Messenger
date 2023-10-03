package com.spyro.messenger.exceptionhandling.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorMessage {
    private int statusCode;
    private LocalDateTime time;
    private String message;
    private String description;
}
