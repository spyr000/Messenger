package com.spyro.messenger.security.emailverification.repo;

import com.spyro.messenger.security.emailverification.entity.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ConfirmationTokenRepo extends JpaRepository<ConfirmationToken, String> {
    @Override
    @NonNull
    Optional<ConfirmationToken> findById(@NonNull String s);

    Optional<ConfirmationToken> findByToken(String token);

    void deleteByUserEmail(String email);
}
