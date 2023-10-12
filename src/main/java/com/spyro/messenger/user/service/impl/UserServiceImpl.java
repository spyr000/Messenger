package com.spyro.messenger.user.service.impl;


import com.spyro.messenger.emailverification.service.EmailSenderService;
import com.spyro.messenger.exceptionhandling.exception.*;
import com.spyro.messenger.security.misc.TokenType;
import com.spyro.messenger.security.repo.SessionRepo;
import com.spyro.messenger.security.service.AuthenticationService;
import com.spyro.messenger.security.service.JwtService;
import com.spyro.messenger.user.dto.*;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.entity.UserToDelete;
import com.spyro.messenger.user.entity.UserToDeleteRepo;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final Long accountRecoveryTime;
    //TODO:
    private final Long accountConfirmationTime;
    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final SessionRepo sessionRepo;
    private final EmailSenderService emailSenderService;
    private final UserToDeleteRepo userToDeleteRepo;
    private final TaskScheduler scheduler;
    private final AuthenticationService authenticationService;

    public UserServiceImpl(
            @Value("${app.time-for-account-recovery}")
            String accountRecoveryTime,
            @Value("${app.time-for-account-confirmation}")
            String accountConfirmationTime,
            UserRepo userRepo,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            SessionRepo sessionRepo,
            EmailSenderService emailSenderService,
            UserToDeleteRepo userToDeleteRepo,
            TaskScheduler scheduler,
            AuthenticationService authenticationService
    ) {
        this.accountRecoveryTime = Long.parseLong(accountRecoveryTime);
        this.accountConfirmationTime = Long.parseLong(accountConfirmationTime);
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.sessionRepo = sessionRepo;
        this.emailSenderService = emailSenderService;
        this.userToDeleteRepo = userToDeleteRepo;
        this.scheduler = scheduler;
        this.authenticationService = authenticationService;
    }

    @Override
    public UserResponse getUser(String username) {
        var user = userRepo.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
        return UserResponse.fromUser(user);
    }

    @Override
    public UserResponse changeUnimportantInfo(String authHeader, UserInfoChangeRequest request) {
        var user = request.changeUser(extractUser(authHeader));
        user = userRepo.save(user);
        return UserResponse.fromUser(user);
    }

    @Override
    public void changePassword(String authHeader, ChangePasswordRequest request) {
        var user = extractUser(authHeader);
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
    public void changeUsername(String authHeader, ChangeUsernameRequest request) {
        var user = extractUser(authHeader);
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
    public void changeEmail(String authHeader, ChangeEmailRequest request) {
        var user = extractUser(authHeader);
        var newEmail = request.getNewEmail();

        if (Objects.equals(user.getEmail(), newEmail)) {
            throw new UnableToChangeEmailException(HttpStatus.BAD_REQUEST, "New email is equal to the old email");
        } else {
            try {
                emailSenderService.sendAccountChangeEmailMessage(user, request.getNewEmail());
            } catch (MessagingException e) {
                throw new EmailSendingException(HttpStatus.BAD_REQUEST, e.getMessage());
            } catch (UnsupportedEncodingException e) {
                throw new EmailSendingException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    @Override
    public void deactivate(String authHeader) {
        var user = extractUser(authHeader);
        user.setActivated(false);
        userToDeleteRepo.save(new UserToDelete(user, accountRecoveryTime));
        userRepo.save(user);
        scheduleDeleting(user, accountRecoveryTime);
    }


    @Transactional(rollbackOn = Exception.class)
    @Async
    @Override
    public void scheduleDeleting(User user, long delay) {
        scheduler.schedule(
                () -> userRepo.findById(user.getId()).ifPresent(
                        foundUser -> {
                            if (foundUser.isActivated()) {
                                userToDeleteRepo.deleteByUser(foundUser);
                                log.info("User %s deletion request canceled".formatted(foundUser.getUsername()));
                            } else {
                                sessionRepo.deleteAllByUserUsername(foundUser.getUsername());
                                sessionRepo.flush();
                                userToDeleteRepo.deleteByUser(foundUser);
                                userRepo.delete(foundUser);
                                log.info("User %s has been deleted".formatted(foundUser.getUsername()));
                            }
                        }
                ),
                Instant.now().plusMillis(delay)
        );
        var now = LocalDateTime.now();
        log.info("Deleting request for user %s is registered at %s with deleting time at %s"
                .formatted(
                        user.toString(),
                        now.toString(),
                        now.plus(delay, ChronoUnit.MILLIS).toString()
                )
        );
    }
    @Override
    public void recover(String authHeader) {
        var user = extractUser(authHeader);
        if (user.isActivated()) {
            throw new UnableRecoverAccountException(HttpStatus.BAD_REQUEST, "User account is already active");
        }
        userToDeleteRepo.deleteByUser(user);
        user.setActivated(true);
        userRepo.save(user);
    }

    private User extractUser(String authHeader) {
        var username = jwtService.extractUsername(
                JwtService.extractBearerToken(authHeader),
                TokenType.ACCESS
        );
        return userRepo.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(User.class));
    }

    @Transactional(rollbackOn = Exception.class)
    @Async
    @Override
    public void deleteAllUsersToDelete() {
        var userToDeleteEntities = userToDeleteRepo.findAllByDeletingTimeLessThanEqual(LocalDateTime.now());
        List<User> usersToDelete = new ArrayList<>();
        for (var userToDelete: userToDeleteEntities) {
            usersToDelete.add(userToDelete.getUser());
        }
        var deletedUsersAmount = usersToDelete.size();
        for (var user: usersToDelete) {
            sessionRepo.deleteAllByUserUsername(user.getUsername());
        }
        sessionRepo.flush();
        userRepo.deleteAll(usersToDelete);
        userToDeleteRepo.deleteAll(userToDeleteEntities);
        if(deletedUsersAmount > 0) log.info("%d users has been deleted".formatted(deletedUsersAmount));
        scheduleDeletingAllUsersToDelete();
    }

    private void scheduleDeletingAllUsersToDelete() {
        var userToDeleteEntities = userToDeleteRepo.findAllByDeletingTimeGreaterThan(LocalDateTime.now());
        for (var userToDelete: userToDeleteEntities) {
            scheduleDeleting(userToDelete.getUser(), ChronoUnit.MILLIS.between(userToDelete.getDeletingTime(), LocalDateTime.now()));
        }
    }
}
