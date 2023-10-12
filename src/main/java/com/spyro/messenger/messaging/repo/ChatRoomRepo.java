package com.spyro.messenger.messaging.repo;

import com.spyro.messenger.messaging.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepo extends JpaRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySender_IdAndRecipient_id(Long senderId, Long recipientId);
}
