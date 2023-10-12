package com.spyro.messenger.messaging.service;

import com.spyro.messenger.messaging.entity.ChatMessage;

public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);
}
