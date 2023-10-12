package com.spyro.messenger.security.service.impl;


import com.spyro.messenger.exceptionhandling.exception.*;
import com.spyro.messenger.security.deviceinfoparsing.service.DeviceInfoService;
import com.spyro.messenger.security.dto.AuthenticationRequest;
import com.spyro.messenger.security.dto.AuthenticationResponse;
import com.spyro.messenger.security.dto.RefreshJwtRequest;
import com.spyro.messenger.security.dto.RegistrationRequest;
import com.spyro.messenger.emailverification.service.EmailSenderService;
import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.repo.SessionRepo;
import com.spyro.messenger.security.service.AuthenticationService;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.security.service.SessionService;
import com.spyro.messenger.user.entity.Role;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSenderService emailSenderService;
    private final DeviceInfoService deviceInfoService;
    private final SessionService sessionService;
    private final SessionRepo sessionRepo;

    //CHECKED
    @Override
    public User register(RegistrationRequest request) {
        var user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getUsername(),
                request.getFirstName(),
                request.getLastName(),
                Role.USER
        );
        try {
            emailSenderService.sendAccountActivationMessage(user);
        } catch (MessagingException e) {
            throw new EmailSendingException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new EmailSendingException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return user;
    }


    //CHECKED
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest) {
        User user;
        try {
            user = userRepo.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        throw new UsernameNotFoundException("User not found");
                    });
            if (!user.isEnabled()) {
                throw new AccountDisabledException(
                        HttpStatus.UNAUTHORIZED,
                        "The authenticating user account is disabled"
                );
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (BaseException e) {
            throw new BaseException(HttpStatus.UNAUTHORIZED, e.getDescription());
        } catch (Exception e) {
            throw new BaseException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
        var checksum = deviceInfoService.calculateHeadersChecksum(httpServletRequest);
        var session = sessionService.getExistingOrSaveNew(user, checksum);
        var accessToken = jwtService.generateAuthToken(user, session.getId(), TokenType.ACCESS);
        var refreshToken = jwtService.generateAuthToken(user, session.getId(), TokenType.REFRESH);
        return new AuthenticationResponse(accessToken, refreshToken);
    }
    @Override
    public AuthenticationResponse refreshToken(
            RefreshJwtRequest request,
            HttpServletRequest httpServletRequest,
            boolean refreshTokenNeeded
    ) {
        var refreshToken = request.getRefreshToken();
        var user = userRepo.findByUsername(
                jwtService.extractUsername(refreshToken, TokenType.REFRESH)
        ).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.isEnabled()) {
            throw new AccountDisabledException(
                    HttpStatus.UNAUTHORIZED,
                    "The authenticating user account is disabled"
            );
        }
        var checksum = deviceInfoService.calculateHeadersChecksum(httpServletRequest);
        if (!jwtService.isAuthTokenValid(refreshToken, user, checksum, TokenType.REFRESH)) {
            throw new InvalidRefreshTokenException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        var sessionId = jwtService.extractSessionID(refreshToken,TokenType.REFRESH);
        return new AuthenticationResponse(
                jwtService.generateAuthToken(user, sessionId, TokenType.ACCESS),
                refreshTokenNeeded ? jwtService.generateAuthToken(user, sessionId, TokenType.REFRESH) : refreshToken
        );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void logOut(String authHeader) {
        var sessionId = jwtService.extractSessionID(
                JwtService.extractBearerToken(authHeader),
                TokenType.ACCESS
        );
        sessionRepo.deleteById(sessionId);
    }
}
