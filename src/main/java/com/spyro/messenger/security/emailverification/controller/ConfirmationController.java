package com.spyro.messenger.security.emailverification.controller;

import com.spyro.messenger.security.emailverification.service.ConfirmationService;
import com.spyro.messenger.security.emailverification.service.EmailSenderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/api/v1/confirmation")
@RequiredArgsConstructor
@Slf4j
public class ConfirmationController {

    private final ConfirmationService confirmationService;

    @RequestMapping(value = EmailSenderService.CONFIRM_URL, method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(
            ModelAndView modelAndView,
            @RequestParam(EmailSenderService.CONFIRM_PARAMETER_NAME) String confirmationToken
    ) {
        return confirmationService.confirm(modelAndView, confirmationToken);
    }
}
