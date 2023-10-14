package com.spyro.messenger.messaging.repo;

import com.spyro.messenger.messaging.entity.Chat;
import com.spyro.messenger.messaging.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, String> {
    @Query("select m from Message m where m.chat = ?1 order by m.timestamp")
    List<Message> findByChatOrderByTimestampAsc(Chat chat);
}
