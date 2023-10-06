package com.spyro.messenger.security.repo;

import com.spyro.messenger.security.entity.Session;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepo extends JpaRepository<Session, Long> {
    Session findByUser_UsernameAndChecksum(String username, long checksum);
    Session findById(String sessionId);
    void deleteById(String sessionID);
}
