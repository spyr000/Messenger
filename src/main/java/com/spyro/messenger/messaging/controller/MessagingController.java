package com.spyro.messenger.messaging.controller;

import com.spyro.messenger.messaging.entity.ChatMessage;
import com.spyro.messenger.messaging.entity.ChatNotification;
import com.spyro.messenger.messaging.service.ChatMessageService;
import com.spyro.messenger.messaging.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class MessagingController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        System.out.println(chatMessage);
//        var chatId = chatRoomService
//                .getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);
        var chat = chatRoomService.getChat(chatMessage.getSender().getId(), chatMessage.getRecipient().getId());
//        chatMessage.setChatId(chatId.get());
        var userName = Objects.requireNonNull(simpMessageHeaderAccessor.getUser()).getName();

        ChatMessage saved = chatMessageService.save(chatMessage);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(chatMessage.getRecipient().getId()),"/queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSender().getId(),
                        saved.getSender().getUsername()
                )
        );
    }

    @MessageExceptionHandler
    @SendTo("/user/errors")
    public String handleException(IllegalArgumentException e) {
        var message = ("an exception occurred! " + NestedExceptionUtils.getMostSpecificCause(e));
        System.out.println(message);
        return message;
    }
}