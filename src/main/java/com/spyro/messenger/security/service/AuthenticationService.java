package com.spyro.messenger.security.service;


import com.spyro.messenger.security.dto.AuthenticationRequest;
import com.spyro.messenger.security.dto.AuthenticationResponse;
import com.spyro.messenger.security.dto.RefreshJwtRequest;
import com.spyro.messenger.security.dto.RegistrationRequest;
import com.spyro.messenger.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    User register(RegistrationRequest request);

    AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest);

    AuthenticationResponse refreshToken(RefreshJwtRequest request, HttpServletRequest httpServletRequest, boolean refreshTokenNeededp);

    void logOut(String authHeader);
}
