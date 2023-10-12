package com.spyro.messenger.emailverification.service;

import com.spyro.messenger.emailverification.util.Link;
import com.spyro.messenger.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

public interface EmailMessageBuilderService {

    MimeMessage buildMessage(
            String subject,
            String messageText,
            String email,
            User user,
            Link... links
    ) throws MessagingException, UnsupportedEncodingException;
}
