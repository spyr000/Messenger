package com.spyro.messenger.security.emailverification.service;


import com.spyro.messenger.security.emailverification.entity.ConfirmationToken;
import com.spyro.messenger.user.entity.User;
import org.springframework.web.servlet.ModelAndView;

public interface ConfirmationService {
    ConfirmationToken register(User user);

    ModelAndView confirm(ModelAndView modelAndView, String token);
}
