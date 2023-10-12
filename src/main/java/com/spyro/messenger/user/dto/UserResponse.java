package com.spyro.messenger.user.dto;

import com.spyro.messenger.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    private String username;
    private String firstName;
    private String lastName;
    private AdditionalInfoDto additionalInfo;
    public static UserResponse fromUser(User user) {
        var info = user.getAdditionalInfo();
        return new UserResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                info != null ? AdditionalInfoDto.fromAdditionalInfo(info) : null
        );
    }
}