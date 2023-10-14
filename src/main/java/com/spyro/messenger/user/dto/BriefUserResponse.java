package com.spyro.messenger.user.dto;

import com.spyro.messenger.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BriefUserResponse {
    private String username;
    private String firstName;
    private String lastName;
    public static BriefUserResponse fromUser(User user) {
        return new BriefUserResponse(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
