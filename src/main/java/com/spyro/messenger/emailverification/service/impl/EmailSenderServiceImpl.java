package com.spyro.messenger.emailverification.service.impl;


import com.spyro.messenger.emailverification.service.EmailMessageBuilderService;
import com.spyro.messenger.emailverification.util.Link;
import com.spyro.messenger.exceptionhandling.exception.EntityAlreadyExistsException;
import com.spyro.messenger.emailverification.service.EmailSenderService;
import com.spyro.messenger.emailverification.service.ConfirmationService;
import com.spyro.messenger.security.misc.ConfirmationTokenType;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private final JavaMailSender mailSender;
    private final ConfirmationService confirmationTokenService;
    private final UserRepo userRepo;
    @Value("${security.email-verification.confirmation-server-address}")
    private String confirmationServerAddress;

    private final EmailMessageBuilderService emailMessageBuilderService;

    @Override
    public void sendAccountActivationMessage(User user) throws MessagingException, UnsupportedEncodingException {
        if (userRepo.existsByUsername(user.getUsername())) throw new EntityAlreadyExistsException(User.class);
        else {
            userRepo.save(user);
        }
        var token = confirmationTokenService.register(user, ConfirmationTokenType.ACCOUNT_ACTIVATION);
        var message = emailMessageBuilderService.buildMessage(
                "Please verify your registration",
                "Please click the link below to verify your registration:",
                user.getEmail(),
                user,
                Link.builder()
                        .text("VERIFY")
                        .url(buildConfirmRegistrationUrl(confirmationServerAddress, token.getToken()))
                        .build()
        );
        mailSender.send(message);
    }

    @Async
    @Override
    public void sendAccountChangeEmailMessage(User user, String newEmail) throws MessagingException, UnsupportedEncodingException {
        var acceptToken = confirmationTokenService.register(
                user,
                ConfirmationTokenType.EMAIL_CHANGING,
                newEmail
        );
        var newEmailMessage = emailMessageBuilderService.buildMessage(
                "Please verify your email change",
                "Please click the link below to verify email change:",
                newEmail,
                user,
                Link.builder()
                        .text("VERIFY")
                        .url(buildConfirmEmailChangeUrl(confirmationServerAddress, acceptToken.getToken()))
                        .build()
        );
        mailSender.send(newEmailMessage);
    }
}
