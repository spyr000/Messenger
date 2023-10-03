package com.spyro.messenger.security.emailverification.service.impl;


import com.spyro.messenger.exceptionhandling.exception.EntityNotFoundException;
import com.spyro.messenger.security.emailverification.entity.ConfirmationToken;
import com.spyro.messenger.security.emailverification.repo.ConfirmationTokenRepo;
import com.spyro.messenger.security.emailverification.service.RegistrationConfirmationService;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Service
public class RegistrationConfirmationServiceImpl implements RegistrationConfirmationService {
    private final ConfirmationTokenRepo confirmationTokenRepo;
    private final UserRepo userRepo;

    @Override
    public ConfirmationToken register(User user) {
        var token = new ConfirmationToken(user);
        confirmationTokenRepo.save(token);
        return token;
    }

    @Override
    public ModelAndView confirm(ModelAndView modelAndView, String token) {
        var confirmationToken = confirmationTokenRepo.findByToken(token);
                confirmationToken.ifPresentOrElse(
                foundToken -> {
                    var user = userRepo.findByEmailIgnoreCase(
                            foundToken.getUser().getEmail()
                    ).orElseThrow(
                            () -> new EntityNotFoundException(User.class)
                    );
                    user.setEnabled(true);
                    userRepo.save(user); //TODO:
                    modelAndView.setViewName("accountVerified");
                },
                () -> makeConfirmationErrorMV(modelAndView)
        );
        confirmationTokenRepo.deleteByUserEmail(confirmationToken.get().getUser().getEmail());
        return modelAndView;
    }

    private static void makeConfirmationErrorMV(ModelAndView modelAndView) {
        modelAndView.addObject("message", "The link is invalid or broken!");
        modelAndView.setViewName("error");
    }

}
