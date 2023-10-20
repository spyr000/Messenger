package com.spyro.messenger.security.service.impl;

import com.spyro.messenger.annotation.ConfigureTests;
import com.spyro.messenger.emailverification.service.EmailSenderService;
import com.spyro.messenger.security.dto.AuthenticationRequest;
import com.spyro.messenger.security.dto.AuthenticationResponse;
import com.spyro.messenger.security.dto.RefreshJwtRequest;
import com.spyro.messenger.security.dto.RegistrationRequest;
import com.spyro.messenger.security.entity.Session;
import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.repo.SessionRepo;
import com.spyro.messenger.security.service.DeviceInfoService;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.security.service.SessionService;
import com.spyro.messenger.user.entity.Role;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ConfigureTests
class AuthenticationServiceImplTest {
    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private DeviceInfoService deviceInfoService;
    @Mock
    private SessionService sessionService;
    @Mock
    private SessionRepo sessionRepo;
    @InjectMocks
    private AuthenticationServiceImpl authService;
    private User testUser;

    @BeforeEach
    void setupTestUser() {
        testUser = User.builder()
                .username("JohnDoe")
                .password("password")
                .role(Role.USER)
                .build();
        testUser.setActivated(true);
        testUser.setConfirmed(true);
    }

    @Test
    void register() throws MessagingException, UnsupportedEncodingException {
        var testPassword = testUser.getPassword();
        var request = new RegistrationRequest(
                testUser.getUsername(),
                testUser.getEmail(),
                testPassword,
                testUser.getFirstName(),
                testUser.getLastName()
        );
        when(passwordEncoder.encode(eq(testPassword))).thenReturn(testPassword);
        doNothing().when(emailSenderService).sendAccountActivationMessage(any(User.class));
        authService.register(request);
        verify(passwordEncoder).encode(eq(testPassword));
        verify(emailSenderService).sendAccountActivationMessage(any(User.class));
    }

    @Test
    void authenticate() {
        var tokensValue = "test";
        var testLong = 0L;
        var request = new AuthenticationRequest(testUser.getPassword(), testUser.getUsername());
        when(
                userRepo.findByUsername(eq(request.getUsername()))
        ).thenReturn(Optional.of(testUser));
        when(
                authenticationManager.authenticate(any(Authentication.class))
        ).thenReturn(null);
        when(
                deviceInfoService.calculateHeadersChecksum(any(HttpServletRequest.class))
        ).thenReturn(testLong);
        var session = new Session(testUser, testLong);
        session.setId(String.valueOf(testLong));
        when(
                sessionService.getExistingOrSaveNew(any(User.class), eq(testLong))
        ).thenReturn(session);
        when(
                jwtService.generateAuthToken(eq(testUser), eq(String.valueOf(testLong)), any(TokenType.class))
        ).thenReturn(tokensValue);
        var result = authService.authenticate(request, new MockHttpServletRequest());
        assertTrue(
                new ReflectionEquals(result)
                        .matches(new AuthenticationResponse(tokensValue, tokensValue))
        );
        verify(userRepo).findByUsername(eq(request.getUsername()));
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(deviceInfoService).calculateHeadersChecksum(any(HttpServletRequest.class));
        verify(sessionService).getExistingOrSaveNew(any(User.class), eq(testLong));
        verify(jwtService).generateAuthToken(eq(testUser), eq(session.getId()), eq(TokenType.ACCESS));
        verify(jwtService).generateAuthToken(eq(testUser), eq(session.getId()), eq(TokenType.REFRESH));
    }

    @Test
    void refreshToken() {
        var testLong = 0L;
        var request = new RefreshJwtRequest("test");
        when(
                userRepo.findByUsername(eq(testUser.getUsername()))
        ).thenReturn(Optional.of(testUser));
        when(
                jwtService.extractUsername(eq(request.getRefreshToken()), eq(TokenType.REFRESH))
        ).thenReturn(testUser.getUsername());

        when(
                deviceInfoService.calculateHeadersChecksum(any(HttpServletRequest.class))
        ).thenReturn(testLong);
        when(
                jwtService.isAuthTokenValid(eq(request.getRefreshToken()), eq(testUser), eq(testLong), eq(TokenType.REFRESH))
        ).thenReturn(true);
        when(
                jwtService.extractSessionID(eq(request.getRefreshToken()), eq(TokenType.REFRESH))
        ).thenReturn(String.valueOf(testLong));
        var expectedResponse = new AuthenticationResponse(
                "access",
                request.getRefreshToken()
        );
        when(
                jwtService.generateAuthToken(eq(testUser), eq(String.valueOf(testLong)), eq(TokenType.ACCESS))
        ).thenReturn(expectedResponse.getAccessToken());
        assertTrue(
                new ReflectionEquals(
                        authService.refreshToken(request, new MockHttpServletRequest(), false)
                ).matches(expectedResponse)
        );
        expectedResponse.setRefreshToken("refresh");
        when(
                jwtService.generateAuthToken(eq(testUser), eq(String.valueOf(testLong)), eq(TokenType.REFRESH))
        ).thenReturn(expectedResponse.getRefreshToken());
        assertTrue(
                new ReflectionEquals(
                        authService.refreshToken(request, new MockHttpServletRequest(), true)
                ).matches(expectedResponse)
        );
        verify(userRepo, times(2)).findByUsername(eq(testUser.getUsername()));
        verify(jwtService, times(2)).extractUsername(eq(request.getRefreshToken()), eq(TokenType.REFRESH));
        verify(deviceInfoService, times(2)).calculateHeadersChecksum(any(HttpServletRequest.class));
        verify(jwtService, times(2)).isAuthTokenValid(eq(request.getRefreshToken()), eq(testUser), eq(testLong), eq(TokenType.REFRESH));
        verify(jwtService, times(2)).extractSessionID(eq(request.getRefreshToken()), eq(TokenType.REFRESH));
        verify(jwtService, times(3)).generateAuthToken(eq(testUser), eq(String.valueOf(testLong)), any(TokenType.class));
    }

    @Test
    void logOut() {
        var testHeaderValue = "Bearer test";
        var testTokenValue = "test";
        var testSessionId = "id";
        when(
                jwtService.extractSessionID(eq(testTokenValue), eq(TokenType.ACCESS))
        ).thenReturn(testSessionId);
        doNothing().when(sessionRepo).deleteById(eq(testSessionId));
        authService.logOut(testHeaderValue);
        verify(jwtService).extractSessionID(eq(testTokenValue), eq(TokenType.ACCESS));
        verify(sessionRepo).deleteById(eq(testSessionId));
    }
}