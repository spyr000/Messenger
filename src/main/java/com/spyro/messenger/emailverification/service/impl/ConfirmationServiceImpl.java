package com.spyro.messenger.emailverification.service.impl;


import com.spyro.messenger.emailverification.entity.ConfirmationToken;
import com.spyro.messenger.emailverification.repo.ConfirmationTokenRepo;
import com.spyro.messenger.emailverification.service.ConfirmationService;
import com.spyro.messenger.exceptionhandling.exception.BaseException;
import com.spyro.messenger.security.misc.ConfirmationTokenType;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class ConfirmationServiceImpl implements ConfirmationService {
    private final ConfirmationTokenRepo confirmationTokenRepo;
    private final UserRepo userRepo;

    @Override
    public ConfirmationToken register(User user, ConfirmationTokenType tokenType) {
        ConfirmationToken token;
        try {
            token = new ConfirmationToken(user, tokenType);
        } catch (IOException e) {
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        confirmationTokenRepo.save(token);
        return token;
    }

    @Override
    public ConfirmationToken register(User user, ConfirmationTokenType tokenType, String extraField) {
        ConfirmationToken token;
        try {
            token = new ConfirmationToken(user, tokenType, extraField);
        } catch (IOException e) {
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        confirmationTokenRepo.save(token);
        return token;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ModelAndView confirmRegistration(ModelAndView modelAndView, String token) {
        var confirmationToken = confirmationTokenRepo.findByToken(token);
        confirmationToken.ifPresentOrElse(
                foundToken -> {
                    if (foundToken.getTokenType() != ConfirmationTokenType.ACCOUNT_ACTIVATION) {
                        makeConfirmationErrorMV(modelAndView);
                        return;
                    }
                    var userOptional = userRepo.findByUsername(
                            foundToken.getUser().getUsername()
                    );
                    if (userOptional.isEmpty()) {
                        makeConfirmationErrorMV(modelAndView);
                        return;
                    }
                    var user = userOptional.get();
                    user.setConfirmed(true);
                    userRepo.save(user);
                    confirmationTokenRepo.deleteByUserEmail(user.getEmail());
                    modelAndView.addObject(
                            "message",
                            "Congratulations! Your account has been activated and email is verified!"
                    );
                    modelAndView.setViewName("verified");
                },
                () -> makeConfirmationErrorMV(modelAndView)
        );
        return modelAndView;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ModelAndView confirmEmailChange(ModelAndView modelAndView, String token) {
        var confirmationToken = confirmationTokenRepo.findByToken(token);
        confirmationToken.ifPresentOrElse(
                foundToken -> {
                    if (foundToken.getTokenType() != ConfirmationTokenType.EMAIL_CHANGING) {
                        makeConfirmationErrorMV(modelAndView);
                        return;
                    }
                    var userOptional = userRepo.findByUsername(
                            foundToken.getUser().getUsername()
                    );
                    if (userOptional.isEmpty()) {
                        makeConfirmationErrorMV(modelAndView);
                        return;
                    }
                    var user = userOptional.get();
                    var email = foundToken.getExtraField();
                    if (email == null) {
                        makeConfirmationErrorMV(modelAndView);
                        return;
                    }
                    user.setEmail(email);
                    userRepo.save(user);
                    confirmationTokenRepo.deleteByUserEmail(user.getEmail());
                    modelAndView.addObject(
                            "message",
                            "Congratulations! Your account email has been changed to %s!".formatted(email)
                    );
                    modelAndView.setViewName("verified");
                },
                () -> makeConfirmationErrorMV(modelAndView)
        );
        return modelAndView;
    }

    private static void makeConfirmationErrorMV(ModelAndView modelAndView) {
        modelAndView.addObject("message", "The link is invalid or broken!");
        modelAndView.setViewName("error");
    }

}
