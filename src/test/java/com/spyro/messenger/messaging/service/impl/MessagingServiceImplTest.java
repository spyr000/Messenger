package com.spyro.messenger.messaging.service.impl;

import com.spyro.messenger.annotation.ConfigureTests;
import com.spyro.messenger.friends.entity.FriendRequest;
import com.spyro.messenger.friends.repo.FriendRequestRepo;
import com.spyro.messenger.messaging.component.WebSocketHandler;
import com.spyro.messenger.messaging.dto.HistoryResponse;
import com.spyro.messenger.messaging.dto.MessageRequest;
import com.spyro.messenger.messaging.dto.MessageResponse;
import com.spyro.messenger.messaging.entity.Chat;
import com.spyro.messenger.messaging.entity.Message;
import com.spyro.messenger.messaging.repo.ChatRepo;
import com.spyro.messenger.messaging.repo.MessageRepo;
import com.spyro.messenger.user.entity.Role;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ConfigureTests
class MessagingServiceImplTest {
    private final String testAuthHeader = "bearer test";
    @Mock
    private UserRepo userRepo;
    @Mock
    private FriendRequestRepo friendRequestRepo;
    @Mock
    private UserService userService;
    @Mock
    private ChatRepo chatRepo;
    @Mock
    private MessageRepo messageRepo;
    @Mock
    private WebSocketHandler webSocketHandler;
    @InjectMocks
    private MessagingServiceImpl messagingService;
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

    @AfterEach
    void verifyExtracting() {
        verify(userService).extractUser(eq(testAuthHeader));
    }

    @Test
    void sendMessage() {
        var request = new MessageRequest("foo");
        var testChat = new Chat(testRequester, testAddressee);
        var testMessage = new Message(testAddressee, request.getContent(), testChat);
        when(
                userRepo.findByUsername(eq(testAddressee.getUsername()))
        ).thenReturn(Optional.of(testAddressee));
        when(
                friendRequestRepo.findByTwoFriends(eq(testRequester), eq(testAddressee))
        ).thenReturn(Optional.of(new FriendRequest()));
        when(
                chatRepo.findByTwoMembers(eq(testRequester), eq(testAddressee))
        ).thenReturn(Optional.of(testChat));
        when(
                messageRepo.save(any(Message.class))
        ).thenReturn(testMessage);
        when(
                chatRepo.save(any(Chat.class))
        ).thenReturn(null);
        doNothing()
                .when(webSocketHandler)
                .updateSession(eq(testAddressee.getUsername()), any(MessageResponse.class));
        assertDoesNotThrow(
                () -> messagingService.sendMessage(testAuthHeader, testAddressee.getUsername(), request)
        );
        verify(webSocketHandler).updateSession(eq(testAddressee.getUsername()), any(MessageResponse.class));
    }

    @Test
    void getChatHistory() {
        var testChat = new Chat();
        var testHistoryMessages = List.of(
                new Message(testRequester, "firstMessage", testChat),
                new Message(testAddressee, "secondMessage", testChat)
        );
        var expected = new HistoryResponse(testHistoryMessages);
        when(userRepo.findByUsername(eq(testAddressee.getUsername()))).thenReturn(Optional.of(testAddressee));
        when(chatRepo.findByTwoMembers(testRequester, testAddressee)).thenReturn(Optional.of(testChat));
        when(messageRepo.findByChatOrderByTimestampAsc(eq(testChat))).thenReturn(testHistoryMessages);
        assertTrue(
                new ReflectionEquals(
                        expected
                                .getMessages()
                                .toArray()
                ).matches(
                        messagingService
                                .getChatHistory(testAuthHeader, testAddressee.getUsername())
                                .getMessages()
                                .toArray()
                )
        );
    }
}