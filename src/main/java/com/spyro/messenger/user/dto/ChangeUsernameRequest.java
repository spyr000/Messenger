package com.spyro.messenger.user.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUsernameRequest {
    private String newUsername;
}
