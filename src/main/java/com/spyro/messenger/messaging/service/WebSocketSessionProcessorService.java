package com.spyro.messenger.messaging.service;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketSessionProcessorService {
    void initSession(WebSocketSession session);

    String getAddresseeAttributeName();

    String getSenderAttributeName();

    String getChatAttributeName();
}
