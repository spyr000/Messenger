package com.spyro.messenger.friends.service.impl;

import com.spyro.messenger.annotation.ConfigureTests;
import com.spyro.messenger.exceptionhandling.exception.UnableToProcessFriendRequestException;
import com.spyro.messenger.friends.entity.FriendRequest;
import com.spyro.messenger.friends.entity.FriendRequestCondition;
import com.spyro.messenger.friends.repo.FriendRequestRepo;
import com.spyro.messenger.user.entity.Role;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ConfigureTests
class FriendRequestServiceImplTest {
    private final String testAuthHeader = "bearer test";
    @Mock
    private UserService userService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private FriendRequestRepo friendRequestRepo;
    @InjectMocks
    private FriendRequestServiceImpl friendRequestService;
    private User testRequester;
    private User testAddressee;

    @BeforeEach
    void setUp() {
        testRequester = User.builder()
                .username("JohnDoe")
                .password("password")
                .role(Role.USER)
                .build();
        testRequester.setActivated(true);
        testRequester.setConfirmed(true);
        testAddressee = User.builder()
                .username("JaneDoe")
                .password("password")
                .role(Role.USER)
                .build();
        testAddressee.setConfirmed(true);
        testAddressee.setActivated(true);
        when(userService.extractUser(eq(testAuthHeader))).thenReturn(testRequester);
    }

    @Test
    void send() {
        var expected = new FriendRequest(testRequester, testAddressee);
        expected.setCondition(FriendRequestCondition.SENT);
        when(userRepo.findByUsername(eq(testAddressee.getUsername()))).thenReturn(Optional.of(testAddressee));
        when(friendRequestRepo.findByTwoFriends(eq(testRequester), eq(testAddressee)))
                .thenReturn(Optional.empty());
        when(friendRequestRepo.save(any(FriendRequest.class))).thenReturn(expected);
        assertDoesNotThrow(
                () -> friendRequestService.send(testAuthHeader, testAddressee.getUsername())
        );
        verify(userService).extractUser(eq(testAuthHeader));
        verify(friendRequestRepo).findByTwoFriends(eq(testRequester), eq(testAddressee));
        verify(friendRequestRepo).save(any(FriendRequest.class));
    }

    @Test
    void delete() {
        var expected = new FriendRequest(testRequester, testAddressee);
        expected.setCondition(FriendRequestCondition.SENT);
        when(userRepo.findByUsername(eq(testAddressee.getUsername()))).thenReturn(Optional.of(testAddressee));
        when(friendRequestRepo.findBySenderAndRecipient(eq(testRequester), eq(testAddressee)))
                .thenReturn(Optional.of(expected));
        doNothing().when(friendRequestRepo).delete(any(FriendRequest.class));
        assertDoesNotThrow(
                () -> friendRequestService.delete(testAuthHeader, testAddressee.getUsername())
        );
        verify(userService).extractUser(eq(testAuthHeader));
        verify(friendRequestRepo).findBySenderAndRecipient(eq(testRequester), eq(testAddressee));
        verify(friendRequestRepo).delete(any(FriendRequest.class));
    }

    @Test
    void approveAndDeny() {
        var request = new FriendRequest(testAddressee, testRequester);
        request.setCondition(FriendRequestCondition.SENT);
        when(userRepo.findByUsername(eq(testAddressee.getUsername()))).thenReturn(Optional.of(testAddressee));
        when(friendRequestRepo.findBySenderAndRecipient(eq(testAddressee), eq(testRequester)))
                .thenReturn(Optional.of(request));
        when(friendRequestRepo.save(any(FriendRequest.class))).thenReturn(request);

        assertDoesNotThrow(
                () -> friendRequestService.approve(testAuthHeader, testAddressee.getUsername())
        );

        request.setCondition(FriendRequestCondition.SENT);
        assertDoesNotThrow(
                () -> friendRequestService.deny(testAuthHeader, testAddressee.getUsername())
        );
        verify(userService, times(2)).extractUser(eq(testAuthHeader));
        verify(friendRequestRepo, times(2)).findBySenderAndRecipient(eq(testAddressee), eq(testRequester));
        verify(friendRequestRepo, times(2)).save(eq(request));

        request.setCondition(FriendRequestCondition.APPROVED);
        assertThrows(
                UnableToProcessFriendRequestException.class,
                () -> friendRequestService.approve(testAuthHeader, testAddressee.getUsername())
        );
        request.setCondition(FriendRequestCondition.DENIED);
        assertThrows(
                UnableToProcessFriendRequestException.class,
                () -> friendRequestService.deny(testAuthHeader, testAddressee.getUsername())
        );
    }

    @Test
    void reject() {
        var request = new FriendRequest(testAddressee, testRequester);
        request.setCondition(FriendRequestCondition.APPROVED);
        when(userRepo.findByUsername(eq(testAddressee.getUsername()))).thenReturn(Optional.of(testAddressee));
        when(friendRequestRepo.findByTwoFriends(eq(testRequester), eq(testAddressee)))
                .thenReturn(Optional.of(request));
        when(friendRequestRepo.existsBySenderAndRecipient(eq(testRequester), eq(testAddressee))).thenReturn(true);
        when(friendRequestRepo.save(any(FriendRequest.class))).thenReturn(request);
        assertDoesNotThrow(
                () -> friendRequestService.reject(testAuthHeader, testAddressee.getUsername())
        );
        verify(friendRequestRepo).save(any(FriendRequest.class));
    }
}