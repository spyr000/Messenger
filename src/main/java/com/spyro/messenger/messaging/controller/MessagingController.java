package com.spyro.messenger.messaging.controller;

import com.spyro.messenger.messaging.dto.HistoryResponse;
import com.spyro.messenger.messaging.dto.MessageRequest;
import com.spyro.messenger.messaging.service.MessagingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Messaging", description = "The Messaging API")
@SecurityRequirement(name = "JWT")
public class MessagingController {
    private final MessagingService messagingService;

    @Operation(
            summary = "Sending message to user",
            description = "Allows you to send message to user"
    )
    @PostMapping(value = "/{username}", params = "send")
    public ResponseEntity<?> send(
            @Parameter(name = "username", description = "Requested user username")
            @PathVariable("username")
            String addresseeUsername,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody MessageRequest messageRequest
    ) {
        messagingService.sendMessage(authHeader, addresseeUsername, messageRequest);
        return ResponseEntity.ok("Message sent");
    }

    @Operation(
            summary = "Getting chat history",
            description = "Allows you to get chat history"
    )
    @GetMapping("/{username}")
    public HistoryResponse getChatHistory(
            @Parameter(name = "username", description = "Requested user username")
            @PathVariable("username")
            String addresseeUsername,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return messagingService.getChatHistory(authHeader, addresseeUsername);
    }
}