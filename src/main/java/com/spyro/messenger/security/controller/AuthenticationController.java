package com.spyro.messenger.security.controller;


import com.spyro.messenger.security.dto.AuthenticationRequest;
import com.spyro.messenger.security.dto.AuthenticationResponse;
import com.spyro.messenger.security.dto.RefreshJwtRequest;
import com.spyro.messenger.security.dto.RegistrationRequest;
import com.spyro.messenger.security.emailverification.service.EmailSenderService;
import com.spyro.messenger.security.emailverification.service.RegistrationConfirmationService;
import com.spyro.messenger.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.function.Function;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

//    @Transactional(rollbackOn = Exception.class)
    @PostMapping(
            value = "/register",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<?> register(
            @RequestBody RegistrationRequest request
    ) {
        authenticationService.register(request);
        return ResponseEntity.ok()
                .body("The confirmation link has been successfully sent to your email!");
    }

    @PostMapping(
            value = "/authenticate",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping(
            value = "/token",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<AuthenticationResponse> token(
            @RequestBody RefreshJwtRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(request, false));
    }

    @GetMapping(
            value = "/refresh",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody RefreshJwtRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(request, true));
    }


}
