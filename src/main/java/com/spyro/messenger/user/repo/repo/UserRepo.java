package com.spyro.messenger.user.repo.repo;

import com.spyro.messenger.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<User> findByEmailIgnoreCase(String email);
}
