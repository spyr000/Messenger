package com.spyro.messenger.user.service.impl;

import com.spyro.messenger.annotation.ConfigureTests;
import com.spyro.messenger.emailverification.service.EmailSenderService;
import com.spyro.messenger.exceptionhandling.exception.UnableToChangeEmailException;
import com.spyro.messenger.exceptionhandling.exception.UnableToChangePasswordException;
import com.spyro.messenger.exceptionhandling.exception.UnableToChangeUsernameException;
import com.spyro.messenger.exceptionhandling.exception.UnableToRecoverAccountException;
import com.spyro.messenger.friends.repo.FriendRequestRepo;
import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.repo.SessionRepo;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.user.dto.*;
import com.spyro.messenger.user.entity.Role;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.entity.UserToDelete;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.repo.UserToDeleteRepo;
import com.spyro.messenger.user.service.UserTerminatorService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@ConfigureTests
class UserServiceImplTest {
    private final String testAuthHeader = "bearer test";
    @Mock
    private UserRepo userRepo;
    @Mock
    private UserToDeleteRepo userToDeleteRepo;
    @Mock
    private SessionRepo sessionRepo;
    @Mock
    private FriendRequestRepo friendRequestRepo;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserTerminatorService terminatorService;
    private UserServiceImpl userService;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        openMocks(this).close();
        this.userService = spy(new UserServiceImpl(
                "10000",
                userRepo,
                sessionRepo,
                userToDeleteRepo,
                friendRequestRepo,
                jwtService,
                emailSenderService,
                passwordEncoder,
                terminatorService
        ));
        ReflectionTestUtils.setField(userService, "accountRecoveryTime", 10000L);

        testUser = User.builder()
                .username("foo")
                .password("password")
                .role(Role.USER)
                .email("foo@bar.m")
                .build();
        testUser.setActivated(true);
        testUser.setConfirmed(true);
        assertThrows(
                StringIndexOutOfBoundsException.class,
                () -> userService.extractUser("")
        );
        verify(userService).extractUser(anyString());
        doReturn(testUser).when(userService).extractUser(nullable(String.class));
        when(userRepo.findByUsername(nullable(String.class))).thenReturn(Optional.of(testUser));
    }

    @Test
    void getUser() {
        var testFriends = List.of(User.builder().username("user1").build(), User.builder().username("user2").build());
        var testFriendUsernames = List.of("user1", "user2");
        when(friendRequestRepo.findAllFriends(eq(testUser))).thenReturn(testFriends);

        assertTrue(
                new ReflectionEquals(
                        FullUserResponse.fromUserAndFriends(testUser, testFriendUsernames)
                ).matches(userService.getUser(testAuthHeader, testUser.getUsername()))
        );
    }

    @Test
    void changeUnimportantInfo() {
        var expected = FullUserResponse.fromUser(testUser);
        when(userRepo.save(eq(testUser))).thenReturn(testUser);
        assertTrue(
                new ReflectionEquals(
                        expected
                ).matches(userService.changeUnimportantInfo(testAuthHeader,
                        new UserInfoChangeRequest())
                )
        );
    }

    @Test
    void changePassword() {
        var request = new ChangePasswordRequest(
                "oldPassword", testUser.getPassword()
        );
        doAnswer((invocationOnMock) ->
                invocationOnMock.getArgument(0)
                        .equals(invocationOnMock.getArgument(1)))
                .when(passwordEncoder)
                .matches(eq(request.getNewPassword()), anyString());
        assertThrows(
                UnableToChangePasswordException.class,
                () -> userService.changePassword(testAuthHeader, request)
        );
        request.setNewPassword("newPassword");
        assertThrows(
                UnableToChangePasswordException.class,
                () -> userService.changePassword(testAuthHeader, request)
        );
        request.setOldPassword(testUser.getPassword());
        when(passwordEncoder.encode(eq(request.getNewPassword()))).thenReturn(request.getNewPassword());
        when(userRepo.save(any(User.class))).thenReturn(null);
        assertDoesNotThrow(
                () -> userService.changePassword(testAuthHeader, request)
        );
        verify(passwordEncoder).encode(eq(request.getNewPassword()));
        verify(userRepo).save(any(User.class));
    }

    @Test
    void changeUsername() {
        var request = new ChangeUsernameRequest(testUser.getUsername());
        when(userRepo.existsByUsername(eq(request.getNewUsername()))).thenReturn(false);
        doNothing().when(sessionRepo).deleteAllByUserUsername(eq(testUser.getUsername()));
        doNothing().when(sessionRepo).flush();
        when(userRepo.save(any(User.class))).thenReturn(null);
        assertThrows(
                UnableToChangeUsernameException.class,
                () -> userService.changeUsername(testAuthHeader, request)
        );
        request.setNewUsername("newUsername");
        assertDoesNotThrow(
                () -> userService.changeUsername(testAuthHeader, request)
        );
        verify(userRepo).save(any(User.class));
    }

    @Test
    void changeEmail() throws MessagingException, UnsupportedEncodingException {
        var request = new ChangeEmailRequest(testUser.getEmail());
        when(userRepo.existsByEmailIgnoreCase(eq(request.getNewEmail()))).thenReturn(false);
        doNothing().when(emailSenderService).sendAccountChangeEmailMessage(eq(testUser), eq(request.getNewEmail()));
        assertThrows(
                UnableToChangeEmailException.class,
                () -> userService.changeEmail(testAuthHeader, request)
        );
        request.setNewEmail("newEmail");
        assertDoesNotThrow(
                () -> userService.changeEmail(testAuthHeader, request)
        );
    }

    @Test
    void changeRestrictions() {
        var request = new ChangeUserRestrictionsRequest(
                true, true
        );
        when(userRepo.save(any(User.class))).thenReturn(null);
        assertDoesNotThrow(
                () -> userService.changeRestrictions(testAuthHeader, request)
        );
    }

    @Test
    void deactivate() {
        when(userToDeleteRepo.save(any(UserToDelete.class))).thenReturn(null);
        when(userRepo.save(any(User.class))).thenReturn(null);
        testUser.setActivated(false);
        doNothing().when(terminatorService).scheduleDeleting(eq(testUser), anyLong());
        assertDoesNotThrow(
                () -> userService.deactivate(testAuthHeader)
        );
        verify(userRepo).save(eq(testUser));
        verify(userToDeleteRepo).save(any(UserToDelete.class));
        verify(terminatorService).scheduleDeleting(eq(testUser), anyLong());
    }

    @Test
    void recover() {
        doNothing().when(userToDeleteRepo).deleteByUser(eq(testUser));
        when(userRepo.save(eq(testUser))).thenReturn(null);
        assertThrows(
                UnableToRecoverAccountException.class,
                () -> userService.recover(testAuthHeader)
        );
        testUser.setActivated(false);
        assertDoesNotThrow(
                () -> userService.recover(testAuthHeader)
        );
        verify(userRepo).save(eq(testUser));
    }

    @Test
    void extractUser() {
        when(jwtService.extractUsername(
                        eq(JwtService.extractBearerToken(testAuthHeader)),
                        eq(TokenType.ACCESS)
                )
        ).thenReturn(testUser.getUsername());
        when(userRepo.findByUsername(eq(testUser.getUsername()))).thenReturn(Optional.of(testUser));
        assertDoesNotThrow(
                () -> userService.extractUser(testAuthHeader)
        );
    }
}