package com.spyro.messenger.user.dto;

import com.spyro.messenger.user.entity.AdditionalInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalInfoDto implements Serializable {
    private String bio;
    private String status;
    private String avatarBase64;

    public static AdditionalInfoDto fromAdditionalInfo(AdditionalInfo additionalInfo) {
        return new AdditionalInfoDto(
                additionalInfo.getBio(),
                additionalInfo.getStatus(),
                additionalInfo.getAvatarBase64()
        );
    }
}