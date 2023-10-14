package com.spyro.messenger.user.service.impl;


import com.spyro.messenger.emailverification.service.EmailSenderService;
import com.spyro.messenger.exceptionhandling.exception.*;
import com.spyro.messenger.friends.repo.FriendRequestRepo;
import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.repo.SessionRepo;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.user.dto.*;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.entity.UserToDelete;
import com.spyro.messenger.user.entity.UserToDeleteRepo;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import com.spyro.messenger.user.service.UserTerminatorService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final Long accountRecoveryTime;
    private final UserRepo userRepo;
    private final UserToDeleteRepo userToDeleteRepo;
    private final SessionRepo sessionRepo;
    private final FriendRequestRepo friendRequestRepo;
    private final JwtService jwtService;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final UserTerminatorService terminatorService;
    public UserServiceImpl(
            @Value("${app.time-for-account-recovery}")
            String accountRecoveryTime,
            UserRepo userRepo,
            SessionRepo sessionRepo,
            UserToDeleteRepo userToDeleteRepo,
            FriendRequestRepo friendRequestRepo,
            JwtService jwtService,
            EmailSenderService emailSenderService,
            PasswordEncoder passwordEncoder,
            UserTerminatorService terminatorService
    ) {
        this.accountRecoveryTime = Long.parseLong(accountRecoveryTime);
        this.userRepo = userRepo;
        this.sessionRepo = sessionRepo;
        this.userToDeleteRepo = userToDeleteRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.jwtService = jwtService;
        this.emailSenderService = emailSenderService;
        this.passwordEncoder = passwordEncoder;
        this.terminatorService = terminatorService;
    }
    @Override
    public BriefUserResponse getUser(String requesterAuthHeader, String addresseeUsername) {
        var requester = extractUser(requesterAuthHeader);
        var addressee = userRepo.findByUsername(addresseeUsername)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
        if (requester.getUsername().equals(addresseeUsername)) {
            return FullUserResponse.fromUserAndFriends(
                    addressee,
                    getFriendsUsernames(addressee)
            );
        }
        if (friendRequestRepo.findByTwoFriends(requester, addressee).isEmpty()) {
            return BriefUserResponse.fromUser(addressee);
        }
        if (addressee.getRestrictions().isFriendsHiddenFromEveryone()) {
            return FullUserResponse.fromUser(addressee);
        }
        return FullUserResponse.fromUserAndFriends(
                addressee,
                getFriendsUsernames(addressee)
        );
    }

    @Override
    public FullUserResponse changeUnimportantInfo(String requesterAuthHeader, UserInfoChangeRequest request) {
        var user = request.changeUser(extractUser(requesterAuthHeader));
        user = userRepo.save(user);
        return FullUserResponse.fromUser(user);
    }

    @Override
    public void changePassword(String requesterAuthHeader, ChangePasswordRequest request) {
        var user = extractUser(requesterAuthHeader);
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new UnableToChangePasswordException(HttpStatus.BAD_REQUEST, "New password is equal to the old password");
        }
        if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())
        ) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepo.save(user);
        } else throw new UnableToChangePasswordException(HttpStatus.BAD_REQUEST, "Old password is not correct");
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void changeUsername(String requesterAuthHeader, ChangeUsernameRequest request) {
        var user = extractUser(requesterAuthHeader);
        var newUsername = request.getNewUsername();
        if (Objects.equals(user.getUsername(), newUsername)) {
            throw new UnableToChangeUsernameException(HttpStatus.BAD_REQUEST, "New username is equal to the old username");
        }
        if (userRepo.existsByUsername(newUsername)) {
            throw new UnableToChangeUsernameException(
                    HttpStatus.BAD_REQUEST,
                    "A user with the same username already exists"
            );
        } else {
            sessionRepo.deleteAllByUserUsername(user.getUsername());
            sessionRepo.flush();
            user.setUsername(newUsername);
            userRepo.save(user);
        }
    }

    @Override
    public void changeEmail(String requesterAuthHeader, ChangeEmailRequest request) {
        var user = extractUser(requesterAuthHeader);
        var newEmail = request.getNewEmail();
        if (Objects.equals(user.getEmail(), newEmail)) {
            throw new UnableToChangeEmailException(HttpStatus.BAD_REQUEST, "New email is equal to the old email");
        }
        if (userRepo.existsByEmailIgnoreCase(request.getNewEmail())) {
            throw new UnableToChangeEmailException(HttpStatus.BAD_REQUEST, "User with the same email address already exists");
        }
        try {
            emailSenderService.sendAccountChangeEmailMessage(user, request.getNewEmail());
        } catch (MessagingException e) {
            throw new EmailSendingException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new EmailSendingException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
    @Override
    public void changeRestrictions(String requesterAuthHeader, UserRestrictionsRequest request) {
        var user = extractUser(requesterAuthHeader);
        var messagesRestriction = request.getMessagesAllowedFromFriendsOnly();
        var friendsRestriction = request.getMessagesAllowedFromFriendsOnly();
        var restrictions = user.getRestrictions();
        if (messagesRestriction != null) {
            restrictions.setMessagesAllowedFromFriendsOnly(messagesRestriction);
        }
        if (friendsRestriction != null) {
            restrictions.setFriendsHiddenFromEveryone(friendsRestriction);
        }
        user.setRestrictions(restrictions);
        userRepo.save(user);
    }
    @Override
    public void deactivate(String requesterAuthHeader) {
        var user = extractUser(requesterAuthHeader);
        user.setActivated(false);
        userToDeleteRepo.save(new UserToDelete(user, accountRecoveryTime));
        userRepo.save(user);
        terminatorService.scheduleDeleting(user, accountRecoveryTime);
    }

    @Override
    public void recover(String requesterAuthHeader) {
        var user = extractUser(requesterAuthHeader);
        if (user.isActivated()) {
            throw new UnableToRecoverAccountException(HttpStatus.BAD_REQUEST, "User account is already active");
        }
        userToDeleteRepo.deleteByUser(user);
        user.setActivated(true);
        userRepo.save(user);
    }
    @Override
    public User extractUser(String requesterAuthHeader) {
        var username = jwtService.extractUsername(
                JwtService.extractBearerToken(requesterAuthHeader),
                TokenType.ACCESS
        );
        return userRepo.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class));
    }
    private List<String> getFriendsUsernames(User user) {
        var friends = friendRequestRepo.findAllFriends(user);
        List<String> friendsUsernames = new ArrayList<>();
        for (var friend: friends) {
            friendsUsernames.add(friend.getUsername());
        }
        return friendsUsernames;
    }
}
