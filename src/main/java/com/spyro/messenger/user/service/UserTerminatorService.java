package com.spyro.messenger.user.service;

import com.spyro.messenger.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;

public interface UserTerminatorService {
    @Transactional(rollbackOn = Exception.class)
    @Async
    void deleteAllUsersToDelete();

    @Transactional(rollbackOn = Exception.class)
    @Async
    void scheduleDeleting(User user, long delay);
}
