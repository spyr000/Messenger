package com.spyro.messenger.security.service.impl;



import com.spyro.messenger.exceptionhandling.exception.BaseException;
import com.spyro.messenger.exceptionhandling.exception.EmailSendingException;
import com.spyro.messenger.exceptionhandling.exception.ExpiredRefreshTokenException;
import com.spyro.messenger.security.dto.AuthenticationRequest;
import com.spyro.messenger.security.dto.AuthenticationResponse;
import com.spyro.messenger.security.dto.RefreshJwtRequest;
import com.spyro.messenger.security.dto.RegistrationRequest;
import com.spyro.messenger.security.emailverification.service.EmailSenderService;
import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.service.AuthenticationService;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.user.entity.Role;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.repo.UserRepo;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final EmailSenderService emailSenderService;

    @Override
    public void register(RegistrationRequest request) {
        var user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                Role.USER
        );
        try {
            emailSenderService.sendEmail(user);
        } catch (MessagingException e) {
            throw new EmailSendingException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new EmailSendingException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        var user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var accessToken = jwtService.generateToken(user, TokenType.ACCESS);
        var refreshToken = jwtService.generateToken(user, TokenType.REFRESH);
        return new AuthenticationResponse(accessToken, refreshToken);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshJwtRequest request, boolean refreshTokenNeeded) {
        var refreshToken = request.getRefreshToken();
        var user = userRepo.findByUsername(
                jwtService.extractUsername(refreshToken, TokenType.REFRESH)
        ).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH)) {
            throw new ExpiredRefreshTokenException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }
        return new AuthenticationResponse(
                jwtService.generateToken(user, TokenType.ACCESS),
                refreshTokenNeeded ? jwtService.generateToken(user, TokenType.REFRESH) : refreshToken
        );
    }

//    @Override
//    public void confirmRegistration() {
//
//    }

//    @Override
//    private void sendVerificationLink(User user, String siteUrl) {
//
//    }
}
