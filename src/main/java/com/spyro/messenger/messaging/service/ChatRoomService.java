package com.spyro.messenger.messaging.service;

import com.spyro.messenger.messaging.entity.ChatRoom;

public interface ChatRoomService {
    ChatRoom getChat(Long senderId, Long recipientId);
}
