package com.spyro.messenger.emailverification.controller;

import com.spyro.messenger.emailverification.util.ConfirmationUrls;
import com.spyro.messenger.emailverification.service.ConfirmationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(ConfirmationUrls.CONFIRMATION_URL)
@RequiredArgsConstructor
@Slf4j
public class ConfirmationController {
    private final ConfirmationService confirmationService;

    @RequestMapping(
            value =  ConfirmationUrls.CONFIRM_REGISTRATION_URL,
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ModelAndView confirmUserAccountRegistration(
            ModelAndView modelAndView,
            @RequestParam(ConfirmationUrls.CONFIRMATION_PARAMETER_NAME) String confirmationToken
    ) {
        return confirmationService.confirmRegistration(modelAndView, confirmationToken);
    }

    @RequestMapping(
            value =  ConfirmationUrls.CONFIRM_EMAIL_CHANGE_URL,
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ModelAndView confirmEmailChange(
            ModelAndView modelAndView,
            @RequestParam(ConfirmationUrls.CONFIRMATION_PARAMETER_NAME) String confirmationToken
    ) {
        return confirmationService.confirmEmailChange(modelAndView, confirmationToken);
    }
}
