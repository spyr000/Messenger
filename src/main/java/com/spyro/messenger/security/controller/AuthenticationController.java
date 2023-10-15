package com.spyro.messenger.security.controller;


import com.spyro.messenger.security.dto.AuthenticationRequest;
import com.spyro.messenger.security.dto.AuthenticationResponse;
import com.spyro.messenger.security.dto.RefreshJwtRequest;
import com.spyro.messenger.security.dto.RegistrationRequest;
import com.spyro.messenger.security.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "The Authentication API")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "User registration",
            description = "Allows you to register a user"
    )
    @Transactional(rollbackOn = Exception.class)
    @PostMapping(
            value = "/register",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<?> register(
            @RequestBody RegistrationRequest body
    ) {
        authenticationService.register(body);
        return ResponseEntity.ok("The confirmation link has been successfully sent to your email!");
    }

    @Operation(
            summary = "User authentication",
            description = "Allows you to authenticate a user"
    )
    @PostMapping(
            value = "/authenticate",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest body, HttpServletRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(body, request));
    }

    @Operation(
            summary = "Access token refreshing",
            description = "Allows you to refresh an access token"
    )
    @GetMapping(
            value = "/token",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<AuthenticationResponse> token(
            @RequestBody RefreshJwtRequest body, HttpServletRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(body, request, false));
    }

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Refresh and access token refreshing",
            description = "Allows you to refresh both access and refresh tokens token"
    )
    @GetMapping(
            value = "/refresh",
            produces = {"application/json", "application/xml", "application/x-yaml"}
    )
    public ResponseEntity<AuthenticationResponse> refresh(
            @RequestBody RefreshJwtRequest body, HttpServletRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(body, request, true));
    }

    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Account logging out",
            description = "Allows you to log out from your account"
    )
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
