package com.spyro.messenger.messaging.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spyro.messenger.exceptionhandling.exception.SessionAttributeNotSpecifiedException;
import com.spyro.messenger.exceptionhandling.exception.UnableToWriteAsStringException;
import com.spyro.messenger.messaging.dto.MessageRequest;
import com.spyro.messenger.messaging.dto.MessageResponse;
import com.spyro.messenger.messaging.entity.Chat;
import com.spyro.messenger.messaging.service.WebSocketMessagingService;
import com.spyro.messenger.messaging.service.WebSocketSessionProcessorService;
import com.spyro.messenger.user.entity.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler implements SubProtocolCapable {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final WebSocketSessionProcessorService sessionProcessorService;
    private final WebSocketMessagingService messagingService;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.put(Objects.requireNonNull(session.getPrincipal()).getName(), session);
        sessionProcessorService.initSession(session);
        log.info("Server connection opened");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(Objects.requireNonNull(session.getPrincipal()).getName());
        log.info("Server connection closed: " + status);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var messageRequest = objectMapper.readValue(message.getPayload(), MessageRequest.class);
        var addresseeAttribute = Optional.ofNullable(
                session.getAttributes().get(sessionProcessorService.getAddresseeAttributeName())
        ).orElseThrow(
                () -> {
                    throw new SessionAttributeNotSpecifiedException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Addressee attribute not specified"
                    );
                }
        );
        var addresseeUsername = (String) addresseeAttribute;
        var userAttribute = session.getAttributes().get(sessionProcessorService.getSenderAttributeName());
        var chatAttribute = session.getAttributes().get(sessionProcessorService.getChatAttributeName());
        if (userAttribute == null) {
            throw new SessionAttributeNotSpecifiedException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Sender attribute not specified");
        }
        if (chatAttribute == null) {
            throw new SessionAttributeNotSpecifiedException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Chat attribute not specified");
        }
        var messageResponse = messagingService.sendMessageThroughWebSocket((User) userAttribute, (Chat) chatAttribute, messageRequest);
        var addresseeSession = sessions.get(addresseeUsername);
        if (addresseeSession != null) {
            addresseeSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageResponse)));
        }
    }

    public void updateSession(String addresseeUsername, MessageResponse messageResponse) {
        var session = sessions.get(addresseeUsername);
        if (session != null) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageResponse)));
            } catch (IOException e) {
                throw new UnableToWriteAsStringException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
        log.error("Server transport error: " + exception.getMessage(), exception);
    }

    @Override
    @NonNull
    public List<String> getSubProtocols() {
        return Collections.singletonList("subprotocol.demo.websocket");
    }
}