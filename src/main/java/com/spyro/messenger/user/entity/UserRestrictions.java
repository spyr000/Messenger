package com.spyro.messenger.user.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class UserRestrictions {
    private boolean messagesAllowedFromFriendsOnly;
    private boolean friendsHiddenFromEveryone;
    public UserRestrictions() {
        this.messagesAllowedFromFriendsOnly = false;
        this.friendsHiddenFromEveryone = false;
    }
}
