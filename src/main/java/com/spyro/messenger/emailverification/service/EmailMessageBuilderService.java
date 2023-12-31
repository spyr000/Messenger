package com.spyro.messenger.emailverification.service;

import com.spyro.messenger.emailverification.misc.Link;
import com.spyro.messenger.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

public interface EmailMessageBuilderService {
    MimeMessage buildMessage(
            String subject,
            String messageText,
            String email,
            User user,
            Link... links
    ) throws MessagingException, UnsupportedEncodingException;
}
