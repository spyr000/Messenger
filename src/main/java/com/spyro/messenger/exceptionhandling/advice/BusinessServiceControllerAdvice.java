package com.spyro.messenger.exceptionhandling.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spyro.messenger.exceptionhandling.dto.ErrorMessage;
import com.spyro.messenger.exceptionhandling.exception.BaseException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class BusinessServiceControllerAdvice {
    @Value("${spring.mvc.contentnegotiation.parameter-name}")
    private String formatParameterName;
    private final ObjectMapper objectMapper;

    private static ResponseEntity<ErrorMessage> buildResponse(
            Throwable e,
            WebRequest request,
            Function<Throwable, String> messageExtractor,
            Function<Throwable, HttpStatus> httpStatusExtractor
    ) {

        var message = messageExtractor.apply(e);
        var httpStatus = httpStatusExtractor.apply(e);
        log.error(message, e);
        return ResponseEntity
                .status(httpStatus)
                .body(
                        buildErrorMessage(request, message, httpStatus)
                );
    }

    private static ErrorMessage buildErrorMessage(WebRequest request, String message, HttpStatus httpStatus) {
        return ErrorMessage.builder()
                .message(message)
                .description(request.getDescription(false))
                .time(LocalDateTime.now().toString())
                .statusCode(httpStatus.value())
                .build();
    }

    private static ResponseEntity<ErrorMessage> buildResponseForNonCustomExceptions(
            Throwable e, WebRequest request, HttpStatus status
    ) {
        return buildResponse(e, request, Throwable::getMessage, throwable -> status);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorMessage> handleException(
            BaseException e, WebRequest request
    ) {
        return buildResponse(e, request, throwable -> e.getDescription(), throwable -> e.getHttpStatus());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleException(
            UsernameNotFoundException e, WebRequest request
    ) {
        return buildResponseForNonCustomExceptions(e, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorMessage> handleException(
            SignatureException e, WebRequest request
    ) {
        return buildResponseForNonCustomExceptions(e, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorMessage> handleException(
            ExpiredJwtException e, WebRequest request
    ) {
        return buildResponseForNonCustomExceptions(e, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorMessage> handleException(
            MalformedJwtException e, WebRequest request
    ) {
        return buildResponseForNonCustomExceptions(e, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessage> handleException(
            BadCredentialsException e, WebRequest request
    ) {
        return buildResponseForNonCustomExceptions(e, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleException(
            HttpMessageNotReadableException e, WebRequest request
    ) {
        return buildResponseForNonCustomExceptions(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedEncodingException.class)
    public ResponseEntity<ErrorMessage> handleException(
            UnsupportedEncodingException e, WebRequest request
    ) {
        return buildResponseForNonCustomExceptions(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorMessage> handleException(
            MessagingException e, WebRequest request
    ) {
        return buildResponseForNonCustomExceptions(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<String> handleException(
            HttpMediaTypeNotAcceptableException e, WebRequest request
    ) throws JsonProcessingException {
        var messageText = request.getParameter(formatParameterName) + "MIME type not supported as a content negotiation type";
        log.error(messageText, e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(objectMapper
                        .writeValueAsString(
                                buildErrorMessage(request, messageText, HttpStatus.BAD_REQUEST)
                        )
                );
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorMessage> handleException(
            Throwable e, WebRequest request
    ) {
        return buildResponseForNonCustomExceptions(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

