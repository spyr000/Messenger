package com.spyro.messenger.messaging.service.impl;

import com.spyro.messenger.exceptionhandling.exception.AddresseeParamNotSpecifiedException;
import com.spyro.messenger.exceptionhandling.exception.EntityNotFoundException;
import com.spyro.messenger.messaging.service.WebSocketMessagingService;
import com.spyro.messenger.messaging.service.WebSocketSessionProcessorService;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class WebSocketSessionProcessorServiceImpl implements WebSocketSessionProcessorService {
    private static final String ADDRESSEE_USERNAME_PARAM_NAME = "addressee";
    private static final String SENDER_ATTRIBUTE_NAME = "sender";
    private static final String CHAT_ATTRIBUTE_NAME = "chat";
    private final UserRepo userRepo;
    private final WebSocketMessagingService messagingService;

    private String extractAddresseeUsername(String uri) {
        var pattern = Pattern.compile("\\??%s=([^&.]+)&?".formatted(ADDRESSEE_USERNAME_PARAM_NAME));
        var matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new AddresseeParamNotSpecifiedException(
                    HttpStatus.BAD_REQUEST,
                    "Addressee param not specified in request url"
            );
        }
    }

    @Override
    public void initSession(WebSocketSession session) {
        var senderUsername = Objects.requireNonNull(session.getPrincipal()).getName();
        var query = Objects.requireNonNull(session.getUri()).getQuery();
        var addresseeUsername = extractAddresseeUsername(query);
        session.getAttributes().put(
                ADDRESSEE_USERNAME_PARAM_NAME,
                addresseeUsername
        );
        var sender = userRepo.findByUsername(senderUsername).orElseThrow(() -> {
            throw new EntityNotFoundException(User.class);
        });
        session.getAttributes().put(SENDER_ATTRIBUTE_NAME, sender);
        var chat = messagingService.initChatThroughWebSocket(sender, addresseeUsername);
        session.getAttributes().put(CHAT_ATTRIBUTE_NAME, chat);
    }

    @Override
    public String getAddresseeAttributeName() {
        return ADDRESSEE_USERNAME_PARAM_NAME;
    }

    @Override
    public String getSenderAttributeName() {
        return SENDER_ATTRIBUTE_NAME;
    }

    @Override
    public String getChatAttributeName() {
        return CHAT_ATTRIBUTE_NAME;
    }
}
