package com.spyro.messenger.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRestrictionsRequest {
    private Boolean messagesAllowedFromFriendsOnly;
    private Boolean friendsHiddenFromEveryone;
}
