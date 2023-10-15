package com.spyro.messenger.exceptionhandling.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorMessage {
    private int statusCode;
    private String time;
    private String message;
    private String description;
}
