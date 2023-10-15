package com.spyro.messenger.friends.controller;

import com.spyro.messenger.friends.misc.FriendRequestsParams;
import com.spyro.messenger.friends.service.FriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@Tag(name = "Friends", description = "The Friendship operations API")
@SecurityRequirement(name = "JWT")
public class FriendsController {
    private final FriendRequestService friendRequestService;

    @Operation(
            summary = "Friend sending request",
            description = "Allows you to send someone friend request"
    )
    @Parameter(name = "sent", description = "Use it if you want to request your sent friend requests")
    @GetMapping(value = "/requests", params = "sent")
    public ResponseEntity<Map<String, String>> getMyFriendRequests(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(friendRequestService.getMySentFriendRequests(authHeader));
    }

    @Operation(
            summary = "Received friend requests getting",
            description = "Allows you to get your recieved friend requests"
    )
    @Parameter(name = "received", description = "Use it if you want to request your received friend requests")
    @GetMapping(value = "/requests", params = "received")
    public ResponseEntity<Map<String, String>> getMyFriendResponses(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(friendRequestService.getMyReceivedFriendRequests(authHeader));
    }

    @Operation(
            summary = "Sent friend requests processing",
            description = "Allows you to process your sent friend request, depending on \"action\" parameter"
    )
    @PostMapping("/request/{friend-request-recipient-username}")
    public ResponseEntity<String> processFriendRequest(
            @Parameter(name = "friend-request-recipient-username", description = "Friend request recipient username")
            @PathVariable("friend-request-recipient-username") String recipientUsername,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam("action")
            @Parameter(name = "action", description = "Choose between: \"" +
                    FriendRequestsParams.SEND_PARAM_VALUE +
                    "\" and \"" + FriendRequestsParams.DELETE_PARAM_VALUE +
                    "\" to specify your action"
            ) String action
    ) {

        return ResponseEntity.ok(
                friendRequestService.processRequestAction(action, authHeader, recipientUsername)
        );
    }

    @Operation(
            summary = "Received friend requests processing",
            description = "Allows you to process your received friend request, depending on \"action\" parameter"
    )
    @PostMapping(value = "/response/{friend-request-sender-username}")
    public ResponseEntity<String> processFriendResponse(
            @Parameter(name = "friend-request-sender-username", description = "Friend request sender username")
            @PathVariable("friend-request-sender-username")
            String senderUsername,
            @RequestHeader(HttpHeaders.AUTHORIZATION)
            String authHeader,
            @RequestParam("action")
            @Parameter(name = "action", description = "Choose between: \"" +
                    FriendRequestsParams.APPROVE_PARAM_VALUE + "\", \"" + FriendRequestsParams.DELETE_PARAM_VALUE +
                    "\" and \"" + FriendRequestsParams.REJECT_PARAM_VALUE +
                    "\" to specify your action"
            ) String action
    ) {
        return ResponseEntity.ok(
                friendRequestService.processResponseAction(action, authHeader, senderUsername)
        );
    }
}
