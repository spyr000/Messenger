package com.spyro.messenger.messaging.service.impl;

import com.spyro.messenger.messaging.entity.ChatMessage;
import com.spyro.messenger.messaging.repo.ChatMessageRepo;
import com.spyro.messenger.messaging.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {
    private final ChatMessageRepo chatMessageRepo;
    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepo.save(chatMessage);
    }
}
