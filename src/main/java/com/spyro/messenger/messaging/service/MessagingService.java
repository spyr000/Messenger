package com.spyro.messenger.messaging.service;

import com.spyro.messenger.messaging.dto.HistoryResponse;
import com.spyro.messenger.messaging.dto.MessageRequest;

public interface MessagingService {
    void sendMessage(String authHeader, String addresseeUsername, MessageRequest messageRequest);

    HistoryResponse getChatHistory(String authHeader, String addresseeUsername);
}
