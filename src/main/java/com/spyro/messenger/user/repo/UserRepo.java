package com.spyro.messenger.user.repo;

import com.spyro.messenger.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    boolean existsByEmailIgnoreCase(String email);
}
