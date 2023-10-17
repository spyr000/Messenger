package com.spyro.messenger.user.repo;

import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.entity.UserToDelete;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserToDeleteRepo extends JpaRepository<UserToDelete, String> {
    List<UserToDelete> findAllByDeletingTimeLessThanEqual(LocalDateTime now);

    List<UserToDelete> findAllByDeletingTimeGreaterThan(LocalDateTime now);

    @Transactional(rollbackOn = Exception.class)
    void deleteByUser(User user);

}