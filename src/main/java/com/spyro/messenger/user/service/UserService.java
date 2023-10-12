package com.spyro.messenger.user.service;


import com.spyro.messenger.user.dto.*;
import com.spyro.messenger.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;

public interface UserService {
    UserResponse getUser(String username);
    UserResponse changeUnimportantInfo(String authHeader, UserInfoChangeRequest request);
    void changePassword(String authHeader, ChangePasswordRequest request);
    void changeUsername(String authHeader, ChangeUsernameRequest request);
    void changeEmail(String authHeader, ChangeEmailRequest changeEmailRequest);
    void deactivate(String authHeader);
    @Async
    @Transactional(rollbackOn = Exception.class)
    void scheduleDeleting(User user, long delay);

    void recover(String authHeader);

    @Transactional
    void deleteAllUsersToDelete();
}
