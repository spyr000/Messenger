package com.spyro.messenger.security.controller;


import com.spyro.messenger.security.dto.AuthenticationRequest;
import com.spyro.messenger.security.dto.AuthenticationResponse;
import com.spyro.messenger.security.dto.RefreshJwtRequest;
import com.spyro.messenger.security.dto.RegistrationRequest;
import com.spyro.messenger.security.entity.Session;
import com.spyro.messenger.security.service.AuthenticationService;
import com.spyro.messenger.security.service.HttpServletUtilsService;
import com.spyro.messenger.security.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    private final SessionService sessionService;

    @Transactional(rollbackOn = Exception.class)
    @PostMapping(
            value = "/register",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<?> register(
            @RequestBody RegistrationRequest body, HttpServletRequest request
    ) {
        authenticationService.register(body);
        return ResponseEntity.ok()
                .body("The confirmation link has been successfully sent to your email!");
    }

    @PostMapping(
            value = "/authenticate",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest body, HttpServletRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(body, request));
    }

    @GetMapping(
            value = "/token",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<AuthenticationResponse> token(
            @RequestBody RefreshJwtRequest body, HttpServletRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(body, request, false));
    }

    @GetMapping(
            value = "/refresh",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody RefreshJwtRequest body, HttpServletRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(body, request, true));
    }

    @PostMapping(
            value = "/logout",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<?> logOut(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        authenticationService.logOut(authHeader);
        return ResponseEntity.ok().body("You have successfully logged out");
    }
}
