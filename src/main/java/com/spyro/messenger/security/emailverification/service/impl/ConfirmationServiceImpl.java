package com.spyro.messenger.security.emailverification.service.impl;


import com.spyro.messenger.exceptionhandling.exception.EntityNotFoundException;
import com.spyro.messenger.security.emailverification.entity.ConfirmationToken;
import com.spyro.messenger.security.emailverification.repo.ConfirmationTokenRepo;
import com.spyro.messenger.security.emailverification.service.ConfirmationService;
import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.repo.SessionRepo;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.security.service.impl.JwtServiceImpl;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@Service
public class ConfirmationServiceImpl implements ConfirmationService {
    private final ConfirmationTokenRepo confirmationTokenRepo;
    private final UserRepo userRepo;
    private final SessionRepo sessionRepo;

    private final JwtService jwtService;

    @Override
    public ConfirmationToken register(User user) {
        var token = new ConfirmationToken(user);
        confirmationTokenRepo.save(token);
        return token;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
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

                    confirmationTokenRepo.deleteByUserEmail(user.getEmail());
                    modelAndView.setViewName("accountVerified");
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
