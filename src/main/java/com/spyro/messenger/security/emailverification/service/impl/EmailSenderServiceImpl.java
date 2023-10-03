package com.spyro.messenger.security.emailverification.service.impl;


import com.spyro.messenger.exceptionhandling.exception.EntityAlreadyExistsException;
import com.spyro.messenger.security.emailverification.entity.ConfirmationToken;
import com.spyro.messenger.security.emailverification.repo.ConfirmationTokenRepo;
import com.spyro.messenger.security.emailverification.service.EmailSenderService;
import com.spyro.messenger.security.emailverification.service.RegistrationConfirmationService;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class EmailSenderServiceImpl implements EmailSenderService {
    private final JavaMailSender mailSender;

    private final RegistrationConfirmationService confirmationTokenService;

    private final UserService userService;

    private final Function<Resource, String> htmlReader;
    @Value("${spring.mail.username}")
    private String mailSenderEmail;
    @Value("${security.email-verification.mail-sender-name}")
    private String mailSenderName;
    @Value("${security.email-verification.subject}")
    private String mailSubject;
    @Value("${security.email-verification.confirmation-server-address}")
    private String confirmationServerAddress;
    @Value("classpath:emailverification/confirmRegistrationMessageContent.html")
    private Resource emailVerificationMessageContentHtml;
    private final UserRepo userRepo;
    private final ConfirmationTokenRepo confirmationTokenRepo;

    @Async
    @Override
    public void sendEmail(User user) throws MessagingException, UnsupportedEncodingException {

        if (userService.userExists(user)) throw new EntityAlreadyExistsException(User.class);
        else userService.saveUser(user);
        var token = confirmationTokenService.register(user);
        var message = verificationMimeMessageBuilder(user,token);
        mailSender.send(message);
    }

    private MimeMessage verificationMimeMessageBuilder(User user, ConfirmationToken token) throws MessagingException, UnsupportedEncodingException {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message);
        var verifyUrl = buildConfirmRegistrationUrl(confirmationServerAddress, token.getToken());
        var content = htmlReader.apply(emailVerificationMessageContentHtml)
                .replace("[[name]]", user.getUsername())
                .replace("[[URL]]", verifyUrl)
                .replace("[[company]]", this.mailSenderName);
        helper.setFrom(this.mailSenderEmail, this.mailSenderName);
        helper.setTo(user.getEmail());
        helper.setSubject(this.mailSubject);
        helper.setText(content, true);
        return message;
    }
}
