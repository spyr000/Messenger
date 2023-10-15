package com.spyro.messenger.emailverification.service;

import com.spyro.messenger.emailverification.util.ConfirmationUrls;
import com.spyro.messenger.user.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

import java.io.UnsupportedEncodingException;

public interface EmailSenderService {
    default String buildConfirmRegistrationUrl(String confirmationServerAddress, String token) {
        return ConfirmationUrls.FULL_CONFIRMATION_URL_PATTERN
                .formatted(
                        confirmationServerAddress,
                        ConfirmationUrls.CONFIRM_REGISTRATION_URL,
                        token
                );
    }

    default String buildConfirmEmailChangeUrl(String confirmationServerAddress, String token) {
        return ConfirmationUrls.FULL_CONFIRMATION_URL_PATTERN
                .formatted(
                        confirmationServerAddress,
                        ConfirmationUrls.CONFIRM_EMAIL_CHANGE_URL,
                        token
                );
    }

    void sendAccountActivationMessage(User user) throws MessagingException, UnsupportedEncodingException;

    @Async
    void sendAccountChangeEmailMessage(User user, String newEmail) throws MessagingException, UnsupportedEncodingException;
}
