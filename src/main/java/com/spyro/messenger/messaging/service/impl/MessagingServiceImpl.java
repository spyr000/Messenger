package com.spyro.messenger.messaging.service.impl;

import com.spyro.messenger.exceptionhandling.exception.EntityNotFoundException;
import com.spyro.messenger.exceptionhandling.exception.UnableToSendMessageForThisUserException;
import com.spyro.messenger.friends.repo.FriendRequestRepo;
import com.spyro.messenger.messaging.dto.HistoryResponse;
import com.spyro.messenger.messaging.dto.MessageRequest;
import com.spyro.messenger.messaging.dto.MessageResponse;
import com.spyro.messenger.messaging.entity.Chat;
import com.spyro.messenger.messaging.entity.Message;
import com.spyro.messenger.messaging.repo.ChatRepo;
import com.spyro.messenger.messaging.repo.MessageRepo;
import com.spyro.messenger.messaging.service.MessagingService;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {
    private final UserRepo userRepo;
    private final FriendRequestRepo friendRequestRepo;
    private final UserService userService;
    private final ChatRepo chatRepo;
    private final MessageRepo messageRepo;

    @Override
    public void sendMessage(String authHeader, String addresseeUsername, MessageRequest messageRequest) {
        var sender = userService.extractUser(authHeader);
        var addressee = userRepo.findByUsername(addresseeUsername).orElseThrow(
                () -> {
                    throw new EntityNotFoundException(User.class);
                }
        );
        var friends = friendRequestRepo.findByTwoFriends(sender, addressee);
        var chatOptional = chatRepo.findByTwoMembers(sender, addressee);
        if (
                addressee.getRestrictions().isMessagesAllowedFromFriendsOnly() &&
                        friends.isEmpty() &&
                        chatOptional.isEmpty()
        ) {
            throw new UnableToSendMessageForThisUserException(
                    HttpStatus.BAD_REQUEST,
                    "User is restricted"
            );
        }
        var chat = chatOptional.orElse(new Chat(sender, addressee));
        if (chatOptional.isEmpty()) {
            chat = chatRepo.save(chat);
        }
        var message = messageRepo.save(new Message(sender, messageRequest.getContent(), chat));
        chat.addMessage(message);
        chatRepo.save(chat);
    }

    @Override
    public HistoryResponse getChatHistory(String authHeader, String addresseeUsername) {
        {
            var sender = userService.extractUser(authHeader);
            var addressee = userRepo.findByUsername(addresseeUsername).orElseThrow(
                    () -> {
                        throw new EntityNotFoundException(User.class);
                    }
            );
            var chatOptional = chatRepo.findByTwoMembers(sender, addressee);
            if (chatOptional.isPresent()) {
                var messages = messageRepo.findByChatOrderByTimestampAsc(chatOptional.get());
                return new HistoryResponse(messages);
            } else {
                return new HistoryResponse(List.of());
            }
        }
    }

    @Override
    public MessageResponse sendMessageWebSocket(User sender, Chat chat, MessageRequest messageRequest) {
        var messageContent = messageRequest.getContent();
        var message = messageRepo.save(new Message(sender, messageContent, chat));
        chat.addMessage(message);
        chatRepo.save(chat);
        return new MessageResponse(sender.getUsername(), message.getId(), messageContent, LocalDateTime.now());
    }

    @Override
    public Chat initChatWebSocket(User sender, String addresseeUsername) {
        var addressee = userRepo.findByUsername(addresseeUsername).orElseThrow(
                () -> {
                    throw new EntityNotFoundException(User.class);
                }
        );
        var friends = friendRequestRepo.findByTwoFriends(sender, addressee);
        var chatOptional = chatRepo.findByTwoMembers(sender, addressee);
        if (
                addressee.getRestrictions().isMessagesAllowedFromFriendsOnly() &&
                        friends.isEmpty() &&
                        chatOptional.isEmpty()
        ) {
            throw new UnableToSendMessageForThisUserException(
                    HttpStatus.BAD_REQUEST,
                    "User is restricted"
            );
        }
        var chat = chatOptional.orElse(new Chat(sender, addressee));
        if (chatOptional.isEmpty()) {
            chat = chatRepo.save(chat);
        }
        return chat;
    }
}
