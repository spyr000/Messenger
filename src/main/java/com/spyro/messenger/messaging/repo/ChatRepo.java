package com.spyro.messenger.messaging.repo;

import com.spyro.messenger.messaging.entity.Chat;
import com.spyro.messenger.user.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRepo extends JpaRepository<Chat, String> {
    @Transactional(rollbackOn = Exception.class)
    void deleteByInitiatorOrMember(User initiator, User member);
    @Query("select c from Chat c where (c.initiator = ?1 and c.member = ?2) or (c.initiator = ?2 and c.member = ?1)")
    Optional<Chat> findByTwoMembers(User member1, User member2);
}
