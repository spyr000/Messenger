package com.spyro.messenger.messaging.repo;

import com.spyro.messenger.messaging.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, String> {
}
