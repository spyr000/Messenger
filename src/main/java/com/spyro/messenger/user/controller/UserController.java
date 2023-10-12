package com.spyro.messenger.user.controller;

import com.spyro.messenger.user.dto.*;
import com.spyro.messenger.user.service.UserService;
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

    public UserController(
            @Value("${app.time-for-account-recovery}") String accountRecoveryTime,
            UserService userService
    ) {
        this.accountRecoveryTime = Long.parseLong(accountRecoveryTime);
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getByUsername(
            @PathVariable("username") String username
    ) {
        return ResponseEntity.ok(userService.getUser(username));
    }

    @PostMapping("/change-user-info")
    public ResponseEntity<UserResponse> changeUnimportantInfo(
            @RequestBody UserInfoChangeRequest userInfoChangeRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        return ResponseEntity.ok(userService.changeUnimportantInfo(authHeader, userInfoChangeRequest));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.changePassword(authHeader, changePasswordRequest);
        return ResponseEntity.ok("Your password has been successfully changed");
    }

    @PostMapping("/change-username")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangeUsernameRequest changeUsernameRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        userService.changeUsername(authHeader, changeUsernameRequest);
        return ResponseEntity.ok("Your username has been successfully changed");
    }

    @PostMapping("/change-email")
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
