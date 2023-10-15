package com.spyro.messenger.user.service.impl;

import com.spyro.messenger.friends.repo.FriendRequestRepo;
import com.spyro.messenger.messaging.repo.ChatRepo;
import com.spyro.messenger.security.repo.SessionRepo;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.entity.UserToDeleteRepo;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.service.UserTerminatorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTerminatorServiceImpl implements UserTerminatorService {
    private final FriendRequestRepo friendRequestRepo;
    private final UserRepo userRepo;
    private final UserToDeleteRepo userToDeleteRepo;
    private final SessionRepo sessionRepo;
    private final ChatRepo chatRepo;
    private final TaskScheduler scheduler;

    @Transactional(rollbackOn = Exception.class)
    @Async
    @Override
    public void deleteAllUsersToDelete() {
        var userToDeleteEntities = userToDeleteRepo.findAllByDeletingTimeLessThanEqual(LocalDateTime.now());
        List<User> usersToDelete = new ArrayList<>();
        for (var userToDelete : userToDeleteEntities) {
            usersToDelete.add(userToDelete.getUser());
        }
        var deletedUsersAmount = usersToDelete.size();
        for (var user : usersToDelete) {
            sessionRepo.deleteAllByUserUsername(user.getUsername());
        }
        sessionRepo.flush();
        userRepo.deleteAll(usersToDelete);
        userToDeleteRepo.deleteAll(userToDeleteEntities);
        if (deletedUsersAmount > 0) log.info("%d users has been deleted".formatted(deletedUsersAmount));
        scheduleDeletingAllUsersToDelete();
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
                                friendRequestRepo.deleteAllByUser(foundUser);
                                chatRepo.deleteByInitiatorOrMember(user, user);
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

    private void scheduleDeletingAllUsersToDelete() {
        var userToDeleteEntities = userToDeleteRepo.findAllByDeletingTimeGreaterThan(LocalDateTime.now());
        for (var userToDelete : userToDeleteEntities) {
            scheduleDeleting(userToDelete.getUser(), ChronoUnit.MILLIS.between(userToDelete.getDeletingTime(), LocalDateTime.now()));
        }
    }
}
