package com.spyro.messenger.security.repo;

import com.spyro.messenger.security.entity.Session;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionRepo extends JpaRepository<Session, Long> {
    @Query("select s from Session s where s.user.username = ?1")
    List<Session> findByAllByUsername(String username);

    @Query("select s from Session s where s.user.username = ?1 and s.checksum = ?2")
    Session findSessionByUsernameAndChecksum(String username, Long checksum);

    Session findById(String sessionId);

    void deleteById(String sessionID);

    @Transactional(rollbackOn = Exception.class)
    void deleteAllByUserUsername(String username);
}
