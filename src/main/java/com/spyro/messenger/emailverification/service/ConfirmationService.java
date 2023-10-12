package com.spyro.messenger.emailverification.service;


import com.spyro.messenger.emailverification.entity.ConfirmationToken;
import com.spyro.messenger.security.misc.ConfirmationTokenType;
import com.spyro.messenger.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.web.servlet.ModelAndView;

public interface ConfirmationService {
    ConfirmationToken register(User user, ConfirmationTokenType tokenType);

    ConfirmationToken register(User user, ConfirmationTokenType tokenType, String extraField);

    ModelAndView confirmRegistration(ModelAndView modelAndView, String token);

    @Transactional(rollbackOn = Exception.class)
    ModelAndView confirmEmailChange(ModelAndView modelAndView, String token);
}
