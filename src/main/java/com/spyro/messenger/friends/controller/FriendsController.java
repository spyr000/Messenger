package com.spyro.messenger.friends.controller;

import com.spyro.messenger.friends.dto.FriendsDto;
import com.spyro.messenger.friends.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendsController {
    private final FriendRequestService friendRequestService;
    @GetMapping(value = "/requests", params = "sent")
    public ResponseEntity<Map<String,String>> getMyFriendRequests(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(friendRequestService.getMyReceivedFriendRequests(authHeader));
    }
    @GetMapping(value = "/requests", params =  "received")
    public ResponseEntity<Map<String,String>> getMyFriendResponses(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(friendRequestService.getMyReceivedFriendRequests(authHeader));
    }
    @PostMapping("/request/{friend-request-recipient-username}")
    public ResponseEntity<String> processFriendRequest(
            @PathVariable("friend-request-recipient-username") String recipientUsername,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam("action") String action
    ) {

        return ResponseEntity.ok(
                friendRequestService.processRequestAction(action, authHeader, recipientUsername)
        );
    }

    @PostMapping(value = "/response/{friend-request-sender-username}")
    public ResponseEntity<String> processFriendResponse(
            @PathVariable("friend-request-sender-username") String senderUsername,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam("action") String action
    ) {
        return ResponseEntity.ok(
                friendRequestService.processResponseAction(action, authHeader, senderUsername)
        );
    }
}
