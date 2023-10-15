package com.spyro.messenger.messaging.service;

import com.spyro.messenger.messaging.dto.HistoryResponse;
import com.spyro.messenger.messaging.dto.MessageRequest;
import com.spyro.messenger.messaging.dto.MessageResponse;
import com.spyro.messenger.messaging.entity.Chat;
import com.spyro.messenger.user.entity.User;

public interface MessagingService {
    void sendMessage(String authHeader, String addresseeUsername, MessageRequest messageRequest);

    HistoryResponse getChatHistory(String authHeader, String addresseeUsername);

    MessageResponse sendMessageWebSocket(User sender, Chat chat, MessageRequest messageRequest);

    Chat initChatWebSocket(User sender, String addresseeUsername);
}
