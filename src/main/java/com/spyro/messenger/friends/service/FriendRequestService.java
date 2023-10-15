package com.spyro.messenger.friends.service;

import com.spyro.messenger.friends.dto.FriendsDto;

import java.util.Map;

public interface FriendRequestService {
    String processRequestAction(String paramVal, String authHeader, String recipientUsername);

    String processResponseAction(String paramVal, String authHeader, String senderUsername);

    Map<String, String> getMySentFriendRequests(String authHeader);

    Map<String, String> getMyReceivedFriendRequests(String authHeader);

    FriendsDto getFriends(String authHeader, String username);
}
