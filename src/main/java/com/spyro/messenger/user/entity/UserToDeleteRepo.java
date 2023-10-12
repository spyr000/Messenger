package com.spyro.messenger.user.entity;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserToDeleteRepo extends JpaRepository<UserToDelete, String> {
    void deleteAllByDeletingTimeLessThan(LocalDateTime time);
    List<UserToDelete> findAllByDeletingTimeLessThanEqual(LocalDateTime now);

    List<UserToDelete> findAllByDeletingTimeGreaterThan(LocalDateTime now);

    @Transactional(rollbackOn = Exception.class)
    void deleteByUser(User user);

}