package com.spyro.messenger.user.controller;

import com.spyro.messenger.exceptionhandling.exception.EntityNotFoundException;
import com.spyro.messenger.friends.dto.FriendsDto;
import com.spyro.messenger.friends.entity.FriendRequest;
import com.spyro.messenger.friends.repo.FriendRequestRepo;
import com.spyro.messenger.friends.service.FriendRequestService;
import com.spyro.messenger.user.dto.*;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/user")
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

    @GetMapping("/{username}")
    public ResponseEntity<BriefUserResponse> getByUsername(
            @PathVariable("username") String username,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(userService.getUser(authHeader, username));
    }
    @GetMapping(value = "/{username}", params = "friends")
    public ResponseEntity<FriendsDto> getFriendshipList(
            @RequestParam("username") String username,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(friendRequestService.getFriends(authHeader, username));
    }

    @PutMapping(value = "/edit", params = "info")
    public ResponseEntity<FullUserResponse> changeAdditionalInfo(
            @RequestBody UserInfoChangeRequest userInfoChangeRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(userService.changeUnimportantInfo(authHeader, userInfoChangeRequest));
    }

    @PutMapping(value = "/edit", params = "password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.changePassword(authHeader, changePasswordRequest);
        return ResponseEntity.ok("Your password has been successfully changed");
    }

    @PutMapping(value = "/edit", params = "username")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangeUsernameRequest changeUsernameRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.changeUsername(authHeader, changeUsernameRequest);
        return ResponseEntity.ok("Your username has been successfully changed");
    }

    @PutMapping(value = "/edit", params = "email")
    public ResponseEntity<?> changeEmail(
            @RequestBody ChangeEmailRequest changeEmailRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.changeEmail(authHeader, changeEmailRequest);
        return ResponseEntity.ok("The confirmation link has been successfully sent to your email!");
    }

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

    @PostMapping("/recover")
    public ResponseEntity<?> recover(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.recover(authHeader);
        return ResponseEntity.ok("Your account has been successfully recovered");
    }
}
