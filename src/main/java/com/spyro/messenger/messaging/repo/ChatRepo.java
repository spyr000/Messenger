package com.spyro.messenger.messaging.repo;

import com.spyro.messenger.messaging.entity.Chat;
import com.spyro.messenger.messaging.entity.Message;
import com.spyro.messenger.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepo extends JpaRepository<Chat, String> {
    @Query("select c from Chat c where (c.initiator = ?1 and c.member = ?2) or (c.initiator = ?2 and c.member = ?1)")
    Optional<Chat> findByTwoMembers(User member1, User member2);

}
