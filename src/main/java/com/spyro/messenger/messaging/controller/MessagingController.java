package com.spyro.messenger.messaging.controller;

import com.spyro.messenger.exceptionhandling.exception.EntityNotFoundException;
import com.spyro.messenger.exceptionhandling.exception.UnableToSendMessageForThisUserException;
import com.spyro.messenger.friends.repo.FriendRequestRepo;
import com.spyro.messenger.messaging.dto.HistoryResponse;
import com.spyro.messenger.messaging.dto.MessageRequest;
import com.spyro.messenger.messaging.entity.Chat;
import com.spyro.messenger.messaging.entity.Message;
import com.spyro.messenger.messaging.repo.ChatRepo;
import com.spyro.messenger.messaging.repo.MessageRepo;
import com.spyro.messenger.messaging.service.MessagingService;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessagingController {
    private final UserRepo userRepo;
    private final FriendRequestRepo friendRequestRepo;
    private final UserService userService;
    private final MessagingService messagingService;
    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;
    @PostMapping(value = "/{username}", params = "send")
    public ResponseEntity<?> send(
            @PathVariable("username") String addresseeUsername,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody MessageRequest messageRequest
    ) {
        messagingService.sendMessage(authHeader, addresseeUsername, messageRequest);
        return ResponseEntity.ok("Message sent");
    }

    @GetMapping("/{username}")
    public HistoryResponse getChatHistory(
            @PathVariable("username") String addresseeUsername,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return messagingService.getChatHistory(authHeader, addresseeUsername);
    }
}