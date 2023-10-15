package com.spyro.messenger.friends.service.impl;

import com.spyro.messenger.exceptionhandling.exception.EntityNotFoundException;
import com.spyro.messenger.exceptionhandling.exception.UnableToProcessFriendRequestException;
import com.spyro.messenger.friends.dto.FriendsDto;
import com.spyro.messenger.friends.entity.FriendRequest;
import com.spyro.messenger.friends.misc.FriendRequestsParams;
import com.spyro.messenger.friends.repo.FriendRequestRepo;
import com.spyro.messenger.friends.service.FriendRequestService;
import com.spyro.messenger.user.entity.User;
import com.spyro.messenger.user.repo.UserRepo;
import com.spyro.messenger.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
    private final UserService userService;
    private final UserRepo userRepo;
    private final FriendRequestRepo friendRequestRepo;

    @Override
    public String processRequestAction(String paramVal, String authHeader, String recipientUsername) {
        switch (paramVal) {
            case (FriendRequestsParams.SEND_PARAM_VALUE) -> {
                send(authHeader, recipientUsername);
                return "Friend request sent";
            }
            case (FriendRequestsParams.DELETE_PARAM_VALUE) -> {
                delete(authHeader, recipientUsername);
                return "Friend request deleted";
            }
            default -> throw new UnableToProcessFriendRequestException(HttpStatus.BAD_REQUEST, "Inappropriate action");
        }
    }

    @Override
    public String processResponseAction(String paramVal, String authHeader, String senderUsername) {
        switch (paramVal) {
            case (FriendRequestsParams.APPROVE_PARAM_VALUE) -> {
                approve(authHeader, senderUsername);
                return "Friend request approved";
            }
            case (FriendRequestsParams.DENY_PARAM_VALUE) -> {
                deny(authHeader, senderUsername);
                return "Friend request denied";
            }
            case (FriendRequestsParams.REJECT_PARAM_VALUE) -> {
                reject(authHeader, senderUsername);
                return "Friend deleted from your friendship list";
            }
            default -> throw new UnableToProcessFriendRequestException(HttpStatus.BAD_REQUEST, "Inappropriate action");
        }
    }


    public void send(String authHeader, String recipientUsername) {
        var sender = userService.extractUser(authHeader);
        var recipient = userRepo.findByUsername(recipientUsername)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
        if (!friendRequestRepo.existsBySenderAndRecipient(sender, recipient)) {
            friendRequestRepo.save(new FriendRequest(sender, recipient));
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void delete(String authHeader, String recipientUsername) {
        var sender = userService.extractUser(authHeader);
        var recipient = userRepo.findByUsername(recipientUsername)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
        var request = friendRequestRepo.findBySenderAndRecipient(sender, recipient)
                .orElseThrow(() -> new EntityNotFoundException(FriendRequest.class));
        if (!request.isDenied()) {
            friendRequestRepo.delete(request);
        } else
            throw new UnableToProcessFriendRequestException(HttpStatus.BAD_REQUEST, "Unable to delete friend request");
    }

    public void approve(String authHeader, String senderUsername) {
        var recipient = userService.extractUser(authHeader);
        var sender = userRepo.findByUsername(senderUsername)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
        var request = friendRequestRepo.findBySenderAndRecipient(sender, recipient)
                .orElseThrow(() -> new EntityNotFoundException(FriendRequest.class));
        if (request.approve()) {
            friendRequestRepo.save(request);
        } else
            throw new UnableToProcessFriendRequestException(HttpStatus.BAD_REQUEST, "Unable to approve friend request");
    }

    public void deny(String authHeader, String senderUsername) {
        var requester = userService.extractUser(authHeader);
        var sender = userRepo.findByUsername(senderUsername)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
        var request = friendRequestRepo.findBySenderAndRecipient(sender, requester)
                .orElseThrow(() -> new EntityNotFoundException(FriendRequest.class));
        if (request.deny()) {
            friendRequestRepo.save(request);
        } else throw new UnableToProcessFriendRequestException(HttpStatus.BAD_REQUEST, "Unable to deny friend request");
    }

    public void reject(String authHeader, String senderUsername) {
        var requester = userService.extractUser(authHeader);
        var addressee = userRepo.findByUsername(senderUsername)
                .orElseThrow(() -> new EntityNotFoundException(User.class));
        var request = friendRequestRepo.findByTwoFriends(requester, addressee)
                .orElseThrow(() -> new EntityNotFoundException(FriendRequest.class));
        if (request.reject()) {
            if (!friendRequestRepo.existsBySenderAndRecipient(addressee, requester)) {
                var oldSender = request.getSender();
                request.setSender(request.getRecipient());
                request.setRecipient(oldSender);
            }
            friendRequestRepo.save(request);
        } else throw new UnableToProcessFriendRequestException(HttpStatus.BAD_REQUEST, "Unable to reject friendship");
    }

    @Override
    public Map<String, String> getMySentFriendRequests(String authHeader) {
        var sender = userService.extractUser(authHeader);
        var sentRequests = friendRequestRepo.findSentRequests(sender);
        Map<String, String> usersAndRequestConditions = new HashMap<>();
        for (var request : sentRequests) {
            usersAndRequestConditions.put(request.getRecipient().getUsername(), request.getCondition().toString());
        }
        return usersAndRequestConditions;
    }

    @Override
    public Map<String, String> getMyReceivedFriendRequests(String authHeader) {
        var recipient = userService.extractUser(authHeader);
        var sentRequests = friendRequestRepo.findReceivedRequests(recipient);
        Map<String, String> usersAndRequestConditions = new HashMap<>();
        for (var request : sentRequests) {
            usersAndRequestConditions.put(request.getSender().getUsername(), request.getCondition().toString());
        }
        return usersAndRequestConditions;
    }

    @Override
    public FriendsDto getFriends(String authHeader, String username) {
        var user = userService.extractUser(authHeader);
        var requestedUser = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException(User.class);
                });
        if (!user.getUsername().equals(username) && requestedUser.getRestrictions().isFriendsHiddenFromEveryone()) {
            return new FriendsDto(List.of());
        }
        return new FriendsDto(getFriendsUsernames(requestedUser));
    }

    private List<String> getFriendsUsernames(User user) {
        var friends = friendRequestRepo.findAllFriends(user);
        List<String> friendsUsernames = new ArrayList<>();
        for (var friend : friends) {
            friendsUsernames.add(friend.getUsername());
        }
        return friendsUsernames;
    }

}
