package com.spyro.messenger.messaging.service;

import com.spyro.messenger.messaging.dto.MessageRequest;
import com.spyro.messenger.messaging.dto.MessageResponse;
import com.spyro.messenger.messaging.entity.Chat;
import com.spyro.messenger.user.entity.User;

public interface WebSocketMessagingService {
    MessageResponse sendMessageThroughWebSocket(User sender, Chat chat, MessageRequest messageRequest);

    Chat initChatThroughWebSocket(User sender, String addresseeUsername);
}
