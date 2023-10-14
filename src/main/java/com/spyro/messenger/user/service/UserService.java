package com.spyro.messenger.user.service;


import com.spyro.messenger.user.dto.*;
import com.spyro.messenger.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;

public interface UserService {
    BriefUserResponse getUser(String requesterAuthHeader, String addresseeUsername);
    FullUserResponse changeUnimportantInfo(String requesterAuthHeader, UserInfoChangeRequest request);
    void changePassword(String requesterAuthHeader, ChangePasswordRequest request);
    void changeUsername(String requesterAuthHeader, ChangeUsernameRequest request);
    void changeEmail(String requesterAuthHeader, ChangeEmailRequest changeEmailRequest);

    void changeRestrictions(String requesterAuthHeader, UserRestrictionsRequest request);

    void deactivate(String requesterAuthHeader);
    void recover(String requesterAuthHeader);
    User extractUser(String requesterAuthHeader);
}
