package com.spyro.messenger.messaging.service.impl;

import com.spyro.messenger.exceptionhandling.exception.EntityNotFoundException;
import com.spyro.messenger.messaging.entity.ChatRoom;
import com.spyro.messenger.messaging.repo.ChatRoomRepo;
import com.spyro.messenger.messaging.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
    private final ChatRoomRepo chatRoomRepo;
    @Override
    public ChatRoom getChat(Long senderId, Long recipientId) {
        return chatRoomRepo.findBySender_IdAndRecipient_id(senderId,recipientId)
                .orElseThrow(() -> new EntityNotFoundException(ChatRoom.class));
    }
}
