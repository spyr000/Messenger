package com.spyro.messenger.user.dto;

import com.fasterxml.jackson.databind.ser.std.CollectionSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.spyro.messenger.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FullUserResponse extends BriefUserResponse implements Serializable {
    private AdditionalInfoDto additionalInfo;

    private FullUserResponse(String username,
                             String firstName,
                             String lastname,
                             AdditionalInfoDto additionalInfo
    ) {
        super(username, firstName, lastname);
        this.additionalInfo = additionalInfo;
    }

    public static FullUserResponse fromUser(User user) {
        var info = user.getAdditionalInfo();
        return new FullUserResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                info != null ? AdditionalInfoDto.fromAdditionalInfo(info) : null
        );
    }

    @JsonAdapter(CollectionSerializer.class)
    private List<String> friends;

    private FullUserResponse(String username,
                             String firstName,
                             String lastname,
                             AdditionalInfoDto additionalInfo,
                             List<String> friends
    ) {
        super(username, firstName, lastname);
        this.additionalInfo = additionalInfo;
        this.friends = friends;
    }

    public static FullUserResponse fromUserAndFriends(User user, List<String> friends) {
        var info = user.getAdditionalInfo();
        return new FullUserResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                info != null ? AdditionalInfoDto.fromAdditionalInfo(info) : null,
                friends
        );
    }
}