package com.spyro.messenger.security.emailverification.service;

import com.spyro.messenger.user.entity.User;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.io.UnsupportedEncodingException;

public interface EmailSenderService {
    String CONFIRM_URL = "/confirm-account";
    String CONFIRM_PARAMETER_NAME = "token";

    default String buildConfirmRegistrationUrl(String confirmationServerAddress, String token) {

        return new StringBuilder(confirmationServerAddress)
                .append("/api/v1/confirmation")
                .append(EmailSenderService.CONFIRM_URL).append('?')
                .append(EmailSenderService.CONFIRM_PARAMETER_NAME).append('=')
                .append(token)
                .toString();

    }
    @Async
    void sendEmail(User user) throws MessagingException, UnsupportedEncodingException;
}
