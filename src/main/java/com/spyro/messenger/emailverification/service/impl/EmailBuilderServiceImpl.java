package com.spyro.messenger.emailverification.service.impl;

import com.spyro.messenger.emailverification.service.EmailMessageBuilderService;
import com.spyro.messenger.emailverification.service.HtmlBuilderService;
import com.spyro.messenger.emailverification.misc.Link;
import com.spyro.messenger.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class EmailBuilderServiceImpl implements EmailMessageBuilderService {
    private final JavaMailSender mailSender;
    private final HtmlBuilderService htmlBuilderService;
    private final Function<Resource, String> htmlReader;
    @Value("${spring.mail.username}")
    private String mailSenderEmail;
    @Value("${security.email-verification.mail-sender-name}")
    private String mailSenderName;
    @Value("classpath:emailverification/confirmMessageContentPattern.html")
    private Resource confirmMessageContentPattern;

    @Override
    public MimeMessage buildMessage(
            String subject,
            String messageText,
            String email,
            User user,
            Link... links
    ) throws MessagingException, UnsupportedEncodingException {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message);
        var content = htmlBuilderService.htmlContent(
                        htmlReader.apply(confirmMessageContentPattern)
                ).name(user.getUsername())
                .message(messageText)
                .company(mailSenderName)
                .links(links)
                .build();
        helper.setFrom(this.mailSenderEmail, this.mailSenderName);
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(content, true);
        return message;
    }
}
