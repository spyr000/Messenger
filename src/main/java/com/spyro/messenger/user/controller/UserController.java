package com.spyro.messenger.user.controller;

import com.spyro.messenger.friends.dto.FriendsDto;
import com.spyro.messenger.friends.service.FriendRequestService;
import com.spyro.messenger.user.dto.*;
import com.spyro.messenger.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "The User API")
@SecurityRequirement(name = "JWT")
public class UserController {

    private final long accountRecoveryTime;
    private final UserService userService;
    private final FriendRequestService friendRequestService;

    public UserController(
            @Value("${app.time-for-account-recovery}") String accountRecoveryTime,
            UserService userService,
            FriendRequestService friendRequestService
    ) {
        this.accountRecoveryTime = Long.parseLong(accountRecoveryTime);
        this.userService = userService;
        this.friendRequestService = friendRequestService;
    }

    @Operation(
            summary = "Getting user info",
            description = "Allows you to get user info"
    )
    @GetMapping("/{username}")
    public ResponseEntity<BriefUserResponse> getByUsername(
            @Parameter(name = "username", description = "Requested user username")
            @PathVariable("username")
            String username,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(userService.getUser(authHeader, username));
    }

    @Operation(
            summary = "Getting user friendship list",
            description = "Allows you to get friendship list"
    )
    @Parameter(name = "friends", description = "Use it if you only want to get user friend list")
    @GetMapping(value = "/{username}", params = "friends")
    public ResponseEntity<FriendsDto> getFriendshipList(
            @Parameter(name = "username", description = "Requested user username")
            @RequestParam("username")
            String username,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(friendRequestService.getFriends(authHeader, username));
    }

    @Operation(
            summary = "Changing user info",
            description = "Allows you to change user info"
    )
    @Parameter(name = "info", description = "Use it if you want to change user info")
    @PutMapping(value = "/edit", params = "info")
    public ResponseEntity<FullUserResponse> changeAdditionalInfo(
            @RequestBody UserInfoChangeRequest userInfoChangeRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(userService.changeUnimportantInfo(authHeader, userInfoChangeRequest));
    }

    @Operation(
            summary = "Changing user password",
            description = "Allows you to change user password"
    )
    @Parameter(name = "password", description = "Use it if you want to change user password")
    @PutMapping(value = "/edit", params = "password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.changePassword(authHeader, changePasswordRequest);
        return ResponseEntity.ok("Your password has been successfully changed");
    }

    @Operation(
            summary = "Changing user username",
            description = "Allows you to change user username"
    )
    @Parameter(name = "username", description = "Use it if you want to change user username")
    @PutMapping(value = "/edit", params = "username")
    public ResponseEntity<?> changeUsername(
            @RequestBody ChangeUsernameRequest changeUsernameRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.changeUsername(authHeader, changeUsernameRequest);
        return ResponseEntity.ok("Your username has been successfully changed");
    }

    @Operation(
            summary = "Request changing user email",
            description = "Allows you to request changing user email"
    )
    @Parameter(name = "email", description = "Use it if you want to request changing user email")
    @PutMapping(value = "/edit", params = "email")
    public ResponseEntity<?> changeEmail(
            @RequestBody ChangeEmailRequest changeEmailRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.changeEmail(authHeader, changeEmailRequest);
        return ResponseEntity.ok("The confirmation link has been successfully sent to your email!");
    }


    @Operation(
            summary = "Changing user restrictions",
            description = "Allows you to change user restrictions"
    )
    @Parameter(name = "restrictions", description = "Use it if you want to change user restrictions")
    @PutMapping(value = "/edit", params = "restrictions")
    public ResponseEntity<?> changeRestrictions(
            @RequestBody ChangeUserRestrictionsRequest changeUserRestrictionsRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.changeRestrictions(authHeader, changeUserRestrictionsRequest);
        return ResponseEntity.ok("The confirmation link has been successfully sent to your email!");
    }

    @Operation(
            summary = "Deleting user account",
            description = "Allows you to delete user account"
    )
    @PostMapping("/delete")
    public ResponseEntity<?> delete(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.deactivate(authHeader);
        return ResponseEntity.ok("Your request to delete your account has been registered. You can recover your account within the next %d days, %d hours, %d minutes"
                .formatted(
                        TimeUnit.MILLISECONDS.toDays(accountRecoveryTime),
                        TimeUnit.MILLISECONDS.toHours(accountRecoveryTime),
                        TimeUnit.MILLISECONDS.toMinutes(accountRecoveryTime)
                )
        );
    }

    @Operation(
            summary = "Recovering user account",
            description = "Allows you to recover user account the allotted time"
    )
    @PostMapping("/recover")
    public ResponseEntity<?> recover(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.recover(authHeader);
        return ResponseEntity.ok("Your account has been successfully recovered");
    }
}
